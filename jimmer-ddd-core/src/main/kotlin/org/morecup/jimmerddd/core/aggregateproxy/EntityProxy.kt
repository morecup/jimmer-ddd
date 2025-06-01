package org.morecup.jimmerddd.core.aggregateproxy

import org.babyfish.jimmer.Draft
import org.babyfish.jimmer.UnloadedException
import org.babyfish.jimmer.meta.ImmutableProp
import org.babyfish.jimmer.meta.ImmutableType
import org.babyfish.jimmer.meta.TargetLevel
import org.babyfish.jimmer.runtime.DraftContext
import org.babyfish.jimmer.runtime.DraftSpi
import org.babyfish.jimmer.runtime.ImmutableSpi
import org.babyfish.jimmer.runtime.ListDraft
import org.babyfish.jimmer.sql.collection.MutableIdViewList
import org.babyfish.jimmer.sql.fetcher.impl.FetcherImpl
import org.morecup.jimmerddd.core.FindByIdFunction
import org.morecup.jimmerddd.core.JimmerDDDException
import org.morecup.jimmerddd.core.annotation.AggregatedField
import org.morecup.jimmerddd.core.annotation.AggregationType
import org.morecup.jimmerddd.core.annotation.Lazy
import org.slf4j.LoggerFactory
import java.lang.invoke.MethodHandles
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

internal open class EntityProxy(
    private val propNameDraftManager:IPropNameDraftManager,
    private val draftContext: DraftContext,
    private val proxyClass: Class<*>,
    private val findByIdFunction: FindByIdFunction
) {
    constructor(spi: ImmutableSpi,draftContext: DraftContext,proxyClass: Class<*>, findByIdFunction: FindByIdFunction)
            : this(SingleSpiPropNameManager(spi,draftContext),draftContext,proxyClass,findByIdFunction)

    companion object{
        private val log = LoggerFactory.getLogger(EntityProxy::class.java)
    }

    /**
     * 变更后的草稿对象，用于存储变更后的属性值。
     */
    fun getSingleChangedDraft(): DraftSpi{
        return propNameDraftManager.changedDraft
    }

    private val propertiesLazyHasLoadMap = mutableMapOf<String, Boolean>()
    //属性是否已经加载过的map
    private val propertiesHasSetMap = mutableMapOf<String, Boolean>()

    /**
     * 代理对象，用于拦截对属性的访问。
     */
    val proxy: Any by lazy { createProxy() }

    protected open fun createProxy(): Any {
        if (Draft::class.java.isAssignableFrom(proxyClass)){
            return Proxy.newProxyInstance(
                proxyClass.classLoader,
                arrayOf(proxyClass, DraftSpi::class.java),
                ProxyInvocationHandler()
            )
        }else{
            val proxyClassDraft = propNameDraftManager.proxyClass
            return Proxy.newProxyInstance(
                proxyClassDraft.classLoader,
                arrayOf(proxyClassDraft, DraftSpi::class.java),
                ProxyInvocationHandler()
            )
        }
    }

    protected open fun handleOtherMethod(proxy: Any, method: Method, args: Array<Any>?): Pair<Boolean,Any?> {
        return false to null
    }

    inner class ProxyInvocationHandler : InvocationHandler {
        override fun invoke(proxy: Any, method: Method, args: Array<Any>?): Any? {
            toGetterPropNameOrNull(method)?.let {
                return handleGetter(it)
            }
            toSetterPropNameOrNull(method)?.let {
                handleSetter(it, args?.first())
                return null
            }
//            // 添加对 hashCode() 方法的处理
            if (method.name == "hashCode" && method.parameterCount == 0) {
                return System.identityHashCode(proxy)
            }
            if (method.isDefault) {
                // 对于默认方法，我们可以调用它的默认实现
                // 使用MethodHandle来调用默认方法

                return invokeDefaultMethod(proxy,method, args)
            }
            val (success, result) = handleOtherMethod(proxy, method, args)
            if (success) {
                return result
            }
            return method.invoke(propNameDraftManager.changedDraft, *args.orEmpty())
        }
        private val lookups = mutableMapOf<Class<*>, MethodHandles.Lookup>()

        private fun invokeDefaultMethod(proxy:Any?, method:Method, args: Array<Any>?):Any? {
            try {
                val declaringClass = method.declaringClass

                // 2. 获取或创建具有私有访问权限的Lookup
                val lookup = lookups.getOrPut(declaringClass) {
                    // 2.1 使用反射获取Lookup构造器
                    val constructor = MethodHandles.Lookup::class.java
                        .getDeclaredConstructor(Class::class.java)
                        .apply { isAccessible = true } // 解决构造器访问限制

                    // 2.2 创建具有正确访问权限的Lookup
                    constructor.newInstance(declaringClass).`in`(declaringClass)
                }

                // 3. 创建方法句柄并调用
                return lookup.unreflectSpecial(method, declaringClass)
                    .bindTo(proxy)
                    .let { handle ->
                        when {
                            args != null && args.isNotEmpty() -> handle.invokeWithArguments(*args)
                            args != null -> handle.invoke() // 无参方法
                            else -> handle.invoke() // 无参方法
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                throw RuntimeException(e)
            }
        }

        private fun toGetterPropNameOrNull(method: Method): String? {
            if (method.parameterCount == 0 && (method.returnType != Void.TYPE && method.returnType != Unit::class.java)){
                val methodName = method.name
                val propName = if (methodName.startsWith("get")) {
                    methodName.substring(3)
                        .replaceFirstChar { it.lowercase() }
                } else if (methodName.startsWith("is")) {
                    methodName.substring(2)
                        .replaceFirstChar { it.lowercase() }
                } else {
                    methodName
                }
                if (propNameDraftManager.contains(propName)){
                    return propName
                }
            }
            return null
        }

        private fun toSetterPropNameOrNull(method: Method): String? {
            if (method.parameterCount == 1 && (method.returnType == Void.TYPE || method.returnType == Unit::class.java || method.declaringClass == method.returnType)) {
                val methodName = method.name
                if (methodName.startsWith("set")) {
                    var propName = methodName.substring(3)
                        .replaceFirstChar { it.lowercase() }
                    if (propNameDraftManager.contains(propName)){
                        return propName
                    }
                }
            }
            return null
        }

        private fun handleGetter(propName: String,ignoreNotAggregatedField:Boolean = false): Any? {
            val prop = propNameDraftManager.getPropByName(propName)
            val lazy = prop.getAnnotation(Lazy::class.java)
            val hasLoad: Boolean = propertiesLazyHasLoadMap.getOrDefault(propName, false)
            if (lazy != null && !hasLoad){
                propertiesLazyHasLoadMap.put(propName, true)
                return if (prop.isView){
                    handleGetter(prop.idViewBaseProp.name,true)
                }else{
                    reloadAndGetField(prop,ignoreNotAggregatedField)
                }
            }else{
                if (prop.isView){
                    return handleGetter(prop.idViewBaseProp.name,true)?.let {
                        when (it){
                            is ImmutableSpi -> {
                                it.__get(it.__type().idProp.id)
                            }
                            is DelegatedMutableList<*> -> {
                                MutableIdViewList<Any,Any>(prop.targetType,it as List<*>)
                            }
                            else -> throw JimmerDDDException("buildProxyDraft idView ${it::class.simpleName} not supported")
                        }
                    }
                }
                try {
                    return getFoundField(prop,ignoreNotAggregatedField)
                } catch (e: UnloadedException) {
//                        兜底没加载的字段 再次请求sql去访问
                    log.warn("$proxyClass $propName 字段并没有传入，但却被强制重新从数据库加载了!")
                    return reloadAndGetField(prop,ignoreNotAggregatedField)
                }
            }
        }

        private fun handleSetter(propName: String, value: Any?,ignoreNotAggregatedField:Boolean = false) {
            val prop = propNameDraftManager.getPropByName(propName)
            val lazy = prop.getAnnotation(Lazy::class.java)
            if (lazy != null) {
                propertiesLazyHasLoadMap.put(propName, true)
            }
            if (prop.isView){
                handleSetter(prop.idViewBaseProp.name,value?.let {
                    when (it){
                        is List<*> -> {
                            it.map { makeIdOnly(prop.idViewBaseProp.targetType,it) as Any? }
                        }
                        else -> {
                            makeIdOnly(prop.idViewBaseProp.targetType,it)
                        }
                    }
                },true)
                return
            }
            setField(prop,value,ignoreNotAggregatedField)
        }
    }

    private fun setField(prop: ImmutableProp,value: Any?,ignoreNotAggregatedField:Boolean = false){
        val propName = prop.name
        if (prop.isAssociation(TargetLevel.ENTITY)){
            val aggregatedField = prop.getAnnotation(AggregatedField::class.java)
            if (aggregatedField == null|| aggregatedField.type == AggregationType.AGGREGATED||aggregatedField.type == AggregationType.ID_ONLY) {
                val (proxyAssociationDraft, changedAssociationDraft) = buildAssociationDraft(value,prop.targetType.javaClass)
                propertiesHasSetMap.put(propName,true)
                propNameDraftManager.setTempDraftPropValue(propName, proxyAssociationDraft)
                propNameDraftManager.setChangedDraftPropValue(propName, changedAssociationDraft)
                return
            }else if (aggregatedField.type == AggregationType.NON_AGGREGATED&&!ignoreNotAggregatedField){
                throw JimmerDDDException("不是聚合根的字段，不应该能够加载")
            }
        }
        propNameDraftManager.setTempDraftPropValue(propName, value)
        propNameDraftManager.setChangedDraftPropValue(propName, value)
    }

    private fun reloadAndGetField(prop: ImmutableProp,ignoreNotAggregatedField: Boolean = false): Any? {
        val propName = prop.name
        if (prop.isAssociation(TargetLevel.ENTITY)){
            if (prop.targetType != null){
                val aggregatedField = prop.getAnnotation(AggregatedField::class.java)
                if (aggregatedField == null|| aggregatedField.type == AggregationType.AGGREGATED||aggregatedField.type == AggregationType.ID_ONLY) {
                    val reloadValue = associationPropReloadValue(prop)
                    val (proxyAssociationDraft, changedAssociationDraft) = buildAssociationDraft(reloadValue,prop.targetType.javaClass)
                    propertiesHasSetMap.put(propName,true)
                    propNameDraftManager.setTempDraftPropValue(propName, proxyAssociationDraft)
                    propNameDraftManager.setChangedDraftPropValue(propName, changedAssociationDraft)
                    return getField(propName)
                }else if (aggregatedField.type == AggregationType.NON_AGGREGATED&&!ignoreNotAggregatedField){
                    throw JimmerDDDException("不是聚合根的字段，不应该能够懒加载")
                }
            }
        }
        val reloadValue = aggregatePropReloadValue(prop)
        propNameDraftManager.setTempDraftPropValue(propName, reloadValue)
        return reloadValue
    }

    private fun getFoundField(prop: ImmutableProp,ignoreNotAggregatedField:Boolean = false): Any? {
        val propName = prop.name
        if (prop.isAssociation(TargetLevel.ENTITY)){
            if (prop.targetType != null) {
                val aggregatedField = prop.getAnnotation(AggregatedField::class.java)
                if (aggregatedField == null || aggregatedField.type == AggregationType.AGGREGATED || aggregatedField.type == AggregationType.ID_ONLY) {
//                    如果没有加载过，就加载并设置
                    if (!propertiesHasSetMap.getOrDefault(propName, false)) {
                        val tempDraftValue = propNameDraftManager.getTempDraftPropValue(propName)
                        val (proxyAssociationDraft, changedAssociationDraft) = buildAssociationDraft(tempDraftValue,prop.targetType.javaClass)
                        propertiesHasSetMap.put(propName, true)
                        propNameDraftManager.setTempDraftPropValue(propName, proxyAssociationDraft)
                        propNameDraftManager.setChangedDraftPropValue(propName, changedAssociationDraft)
                    }
                    return getField(propName)
                } else if (aggregatedField.type == AggregationType.NON_AGGREGATED && !ignoreNotAggregatedField) {
                    throw JimmerDDDException("不是聚合根的字段，不应该能够加载")
                }
            }
        }
        return propNameDraftManager.getTempDraftPropValue(propName)
    }

    private fun associationPropReloadValue(loadProp: ImmutableProp): Any {
        val type = loadProp.declaringType
        val targetFetcher = FetcherImpl(loadProp.targetType.javaClass).allAggregationFields(arrayListOf(type.javaClass))
        val fetcherImplementor = FetcherImpl(type.javaClass).add(loadProp.name,targetFetcher)
        val idValue = propNameDraftManager.getIdPropValue(loadProp.name)
        val haveLazyListPropBase = findByIdFunction(fetcherImplementor, idValue) as ImmutableSpi
        return haveLazyListPropBase.__get(loadProp.id)
    }

    private fun aggregatePropReloadValue(loadProp: ImmutableProp): Any? {
        val type = loadProp.declaringType
        val fetcherImplementor = FetcherImpl(type.javaClass).add(loadProp.name)
        val idValue = propNameDraftManager.getIdPropValue(loadProp.name)
        val haveLazyListPropBase = findByIdFunction(fetcherImplementor, idValue) as ImmutableSpi
        return haveLazyListPropBase.__get(loadProp.id)
    }

    private fun getField(propName: String): Any? {
        val tempDraftValue = propNameDraftManager.getTempDraftPropValue(propName)
        val changedDraftValue = propNameDraftManager.getChangedDraftPropValue(propName)
        if (tempDraftValue is ListDraft<*> && changedDraftValue is ListDraft<*>) {
            val delegatedMutableList =
                DelegatedMutableListCache.getOrPut(tempDraftValue, changedDraftValue as ListDraft<Any>)
            return delegatedMutableList
        } else {
            return propNameDraftManager.getTempDraftPropValue(propName)
        }
    }

    private fun buildAssociationDraft(value: Any?, proxyClass:Class<*>): AssociationDraft {
        if (value == null){
            return AssociationDraft(null,null)
        }
        when (value){
            is ImmutableSpi -> {
                val entityProxy = EntityProxy(value, draftContext, proxyClass, findByIdFunction)
                return AssociationDraft(entityProxy.proxy, entityProxy.getSingleChangedDraft())
            }
            is MutableList<*> -> {
                val newList = value.mapNotNull { item ->
                    EntityProxy(item!! as ImmutableSpi,draftContext,proxyClass,findByIdFunction)
                }
                return AssociationDraft(newList.map { it.proxy }.toMutableList(),newList.map { it.getSingleChangedDraft() }.toMutableList())
            }
            else -> throw JimmerDDDException("buildProxyDraft ${value.let{it::class.simpleName}} not supported")
        }
    }

    private fun makeIdOnly( type:ImmutableType, id: Any?): Any? {
        // 获取类型的 ID 属性
        val idProp = type.idProp
        // 检查 ID 属性是否为空，若为空则抛出异常
        requireNotNull(idProp) { "No id property in \"$type\"" }
        // 若 ID 为空则返回 null
        if (id == null) {
            return null
        }
        val changedDraft = type.draftFactory.apply(draftContext,null).let { it as DraftSpi }
        changedDraft.__set(idProp.id,id)
        return changedDraft.__resolve()
    }

    data class AssociationDraft(
        val proxyAssociationDraft: Any?,
        val changedAssociationDraft: Any?,
    )
}