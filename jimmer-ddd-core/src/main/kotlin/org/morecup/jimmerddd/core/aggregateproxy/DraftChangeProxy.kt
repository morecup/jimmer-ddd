package org.morecup.jimmerddd.core.aggregateproxy

import org.babyfish.jimmer.UnloadedException
import org.babyfish.jimmer.meta.ImmutableProp
import org.babyfish.jimmer.meta.ImmutableType
import org.babyfish.jimmer.meta.TargetLevel
import org.babyfish.jimmer.runtime.DraftContext
import org.babyfish.jimmer.runtime.DraftSpi
import org.babyfish.jimmer.runtime.ImmutableSpi
import org.babyfish.jimmer.runtime.ListDraft
import org.babyfish.jimmer.sql.fetcher.impl.FetcherImpl
import org.morecup.jimmerddd.core.FindByIdFunction
import org.morecup.jimmerddd.core.JimmerDDDConfig
import org.morecup.jimmerddd.core.JimmerDDDException
import org.morecup.jimmerddd.core.annotation.AggregatedField
import org.morecup.jimmerddd.core.annotation.AggregationType
import org.morecup.jimmerddd.core.annotation.Lazy
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class DraftChangeProxy(
    private val spi: ImmutableSpi,
    private val draftContext: DraftContext,
    private val proxyClass: Class<*>,
    private val findByIdFunction: FindByIdFunction = JimmerDDDConfig.getFindByIdFunction()
) {
    companion object{
        private val log = LoggerFactory.getLogger(DraftChangeProxy::class.java)
    }

    private val type = spi.__type()
    private val propNames = type.props.keys
    private val tempDraft = draftContext.toDraftObject<Any>(spi).let { it as DraftSpi }

    /**
     * 变更后的草稿对象，用于存储变更后的属性值。
     */
    val changedDraft = type.draftFactory.apply(draftContext, null).let {
        it as DraftSpi
        it.__set(type.idProp.id,tempDraft.__get(type.idProp.id))
        it
    }
    private val propertiesLazyHasLoadMap = mutableMapOf<String, Boolean>()
    //属性是否已经加载过的map
    private val propertiesHasSetMap = mutableMapOf<String, Boolean>()

    /**
     * 代理对象，用于拦截对属性的访问。
     */
    val proxy: Any by lazy { createProxy() }

    private fun createProxy(): Any {
        return Proxy.newProxyInstance(
            proxyClass.classLoader,
            arrayOf(proxyClass, DraftSpi::class.java),
            ProxyInvocationHandler()
        )
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
            return method.invoke(changedDraft, args)
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
                if (propNames.contains(propName)){
                    return propName
                }
            }
            return null
        }

        private fun toSetterPropNameOrNull(method: Method): String? {
            if (method.parameterCount == 1 && (method.returnType == Void.TYPE || method.returnType == Unit::class.java)) {
                val methodName = method.name
                if (methodName.startsWith("set")) {
                    var propName = methodName.substring(3)
                        .replaceFirstChar { it.lowercase() }
                    if (propNames.contains(propName)){
                        return propName
                    }
                }
            }
            return null
        }

        private fun handleGetter(propName: String,ignoreNotAggregatedField:Boolean = false): Any? {
            val prop = type.props[propName]!!
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
                    return handleGetter(prop.idViewBaseProp.name,true)
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
            val prop = type.props[propName]!!
            val lazy = prop.getAnnotation(Lazy::class.java)
            if (lazy != null) {
                propertiesLazyHasLoadMap.put(propName, true)
            }
            if (prop.isView){
                handleSetter(prop.idViewBaseProp.name,value?.let {
                    when (it){
                        is List<*> -> {
                            it.map { makeIdOnly(prop.targetType,it) as Any? }
                        }
                        else -> {
                            makeIdOnly(prop.targetType,it)
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
                tempDraft.__set(propName, proxyAssociationDraft)
                changedDraft.__set(propName, changedAssociationDraft)
                return
            }else if (aggregatedField.type == AggregationType.NON_AGGREGATED&&!ignoreNotAggregatedField){
                throw JimmerDDDException("不是聚合根的字段，不应该能够加载")
            }
        }
        tempDraft.__set(propName, value)
        changedDraft.__set(propName, value)
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
                    tempDraft.__set(propName, proxyAssociationDraft)
                    changedDraft.__set(propName, changedAssociationDraft)
                    return getField(propName)
                }else if (aggregatedField.type == AggregationType.NON_AGGREGATED&&!ignoreNotAggregatedField){
                    throw JimmerDDDException("不是聚合根的字段，不应该能够懒加载")
                }
            }
        }
        val reloadValue = aggregatePropReloadValue(prop)
        tempDraft.__set(propName, reloadValue)
        return reloadValue
    }

    private fun getFoundField(prop: ImmutableProp,ignoreNotAggregatedField:Boolean = false): Any? {
        val propName = prop.name
        if (prop.isAssociation(TargetLevel.ENTITY)){
            if (prop.targetType != null) {
                val aggregatedField = prop.annotations.filterIsInstance<AggregatedField>().firstOrNull()
                if (aggregatedField == null || aggregatedField.type == AggregationType.AGGREGATED || aggregatedField.type == AggregationType.ID_ONLY) {
//                    如果没有加载过，就加载并设置
                    if (!propertiesHasSetMap.getOrDefault(propName, false)) {
                        val tempDraftValue = tempDraft.__get(propName)
                        val (proxyAssociationDraft, changedAssociationDraft) = buildAssociationDraft(tempDraftValue,prop.targetType.javaClass)
                        propertiesHasSetMap.put(propName, true)
                        tempDraft.__set(propName, proxyAssociationDraft)
                        changedDraft.__set(propName, changedAssociationDraft)
                    }
                    return getField(propName)
                } else if (aggregatedField.type == AggregationType.NON_AGGREGATED && !ignoreNotAggregatedField) {
                    throw JimmerDDDException("不是聚合根的字段，不应该能够加载")
                }
            }
        }
        return tempDraft.__get(propName)
    }

    private fun associationPropReloadValue(loadProp: ImmutableProp): Any {
        val targetFetcher = FetcherImpl(loadProp.targetType.javaClass).allAggregationFields(arrayListOf(type.javaClass))
        val fetcherImplementor = FetcherImpl(type.javaClass).add(loadProp.name,targetFetcher)
        val idValue = spi.__get(type.idProp.id)
        val haveLazyListPropBase = findByIdFunction(fetcherImplementor, idValue) as ImmutableSpi
        return haveLazyListPropBase.__get(loadProp.id)
    }

    private fun aggregatePropReloadValue(loadProp: ImmutableProp): Any? {
        val fetcherImplementor = FetcherImpl(type.javaClass).add(loadProp.name)
        val idValue = spi.__get(type.idProp.id)
        val haveLazyListPropBase = findByIdFunction(fetcherImplementor, idValue) as ImmutableSpi
        return haveLazyListPropBase.__get(loadProp.id)
    }

    private fun getField(propName: String): Any? {
        val tempDraftValue = tempDraft.__get(propName)
        val changedDraftValue = changedDraft.__get(propName)
        if (tempDraftValue is ListDraft<*> && changedDraftValue is ListDraft<*>) {
            val delegatedMutableList =
                DelegatedMutableListCache.getOrPut(tempDraftValue, changedDraftValue as ListDraft<Any>)
            return delegatedMutableList
        } else {
            return tempDraft.__get(propName)
        }
    }

    private fun buildAssociationDraft(value: Any?, proxyClass:Class<*>): AssociationDraft {
        if (value == null){
            return AssociationDraft(null,null)
        }
        when (value){
            is ImmutableSpi -> {
                val draftChangeProxy = DraftChangeProxy(value, draftContext, proxyClass, findByIdFunction)
                return AssociationDraft(draftChangeProxy.proxy,draftChangeProxy.changedDraft)
            }
            is MutableList<*> -> {
                val newList = value.mapNotNull { item ->
                    DraftChangeProxy(item!! as ImmutableSpi,draftContext,proxyClass,findByIdFunction)
                }
                return AssociationDraft(newList.map { it.proxy }.toMutableList(),newList.map { it.changedDraft }.toMutableList())
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