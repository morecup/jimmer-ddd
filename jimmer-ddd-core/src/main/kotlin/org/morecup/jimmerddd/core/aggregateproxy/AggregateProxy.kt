package org.morecup.jimmerddd.core.aggregateproxy

import org.babyfish.jimmer.UnloadedException
import org.babyfish.jimmer.meta.ImmutableProp
import org.babyfish.jimmer.meta.TargetLevel
import org.babyfish.jimmer.runtime.DraftContext
import org.babyfish.jimmer.runtime.DraftSpi
import org.babyfish.jimmer.runtime.ImmutableSpi
import org.babyfish.jimmer.runtime.ListDraft
import org.babyfish.jimmer.sql.fetcher.impl.FetcherImpl
import org.morecup.jimmerddd.core.aggregateproxy.GlobalContext.usingDraftContext
import org.morecup.jimmerddd.core.annotation.AggregatedField
import org.morecup.jimmerddd.core.annotation.AggregationType
import org.morecup.jimmerddd.core.annotation.Lazy
import org.morecup.jimmerddd.core.FindByIdFunction
import org.morecup.jimmerddd.core.JimmerDDDConfig
import org.morecup.jimmerddd.core.JimmerDDDException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.Proxy
import java.util.concurrent.Callable
import kotlin.jvm.java

/**
 * 查看其它特殊字段是否存在问题
 */
class AggregateProxy<P : Any> @JvmOverloads constructor(
    private val implInterfaceClass: Class<P>,
    private val findByIdFunction: FindByIdFunction = JimmerDDDConfig.getFindByIdFunction(),
) {
    companion object {
        val log: Logger= LoggerFactory.getLogger(AggregateProxy::class.java)
    }

    fun <T,R> exec(base: T, implProcessor: (P) -> R): Pair<T, R> {
        val spi = base as ImmutableSpi
        val type = spi.__type()
        val draftContext = DraftContext(null)
        val (proxyDraft, changedDraft) = buildProxyDraftFromNoList(draftContext, base as Any,implInterfaceClass)
        val result = usingDraftContext(draftContext){
            implProcessor(proxyDraft as P)
        }
//        val contextChanged = Internal.produce(type, (changedDraft as DraftSpi).__resolve(), null) as T
//        return contextChanged to result
        val changed = (changedDraft as DraftSpi).__resolve() as T
        draftContext.dispose()
        return changed to result
    }

    fun associationPropReloadValue(spi:ImmutableSpi, loadProp: ImmutableProp): Any {
        val type = spi.__type()
        val targetFetcher = FetcherImpl(loadProp.targetType.javaClass).allAggregationFields(arrayListOf(type.javaClass))
        val fetcherImplementor = FetcherImpl(type.javaClass).add(loadProp.name,targetFetcher)
        val idValue = spi.__get(type.idProp.id)
        val haveLazyListPropBase = findByIdFunction(fetcherImplementor, idValue) as ImmutableSpi
        return haveLazyListPropBase.__get(loadProp.id)
    }

    fun aggregatePropReloadValue(spi:ImmutableSpi, loadProp: ImmutableProp): Any? {
        val type = spi.__type()
        val fetcherImplementor = FetcherImpl(type.javaClass).add(loadProp.name)
        val idValue = spi.__get(type.idProp.id)
        val haveLazyListPropBase = findByIdFunction(fetcherImplementor, idValue) as ImmutableSpi
        return haveLazyListPropBase.__get(loadProp.id)
    }

    fun reloadAndGetField(prop: ImmutableProp,tempDraft:DraftSpi,changedDraft:DraftSpi,spi:ImmutableSpi,propertiesHasSetMap: MutableMap<String, Boolean>,draftContext:DraftContext): Any? {
        val propName = prop.name
        if (prop.isAssociation(TargetLevel.ENTITY)){
            //                            判断是否是别的表的关联字段
            if (prop.targetType != null){

                val aggregatedField = prop.annotations.filterIsInstance<AggregatedField>().firstOrNull()
                if (aggregatedField == null|| aggregatedField.type == AggregationType.AGGREGATED) {
                    val reloadValue = associationPropReloadValue(spi,prop)
                    val (proxyAssociationDraft, changedAssociationDraft) = buildProxyDraft(draftContext, reloadValue)
                    propertiesHasSetMap.put(propName,true)
                    tempDraft.__set(propName, proxyAssociationDraft)
                    changedDraft.__set(propName, changedAssociationDraft)
                    val tempDraftValue = tempDraft.__get(propName)
                    val changedDraftValue = changedDraft.__get(propName)
                    if (tempDraftValue is ListDraft<*> && changedDraftValue is ListDraft<*> ){
                        val delegatedMutableList = DelegatedMutableListCache.getOrPut(tempDraftValue, changedDraftValue as ListDraft<Any>)
                        return delegatedMutableList
                    }else{
                        return tempDraft.__get(propName)
                    }
                }else if (aggregatedField.type == AggregationType.NON_AGGREGATED){
                    throw JimmerDDDException("不是聚合根的字段，不应该能够懒加载")
                }
            }
        }
        val reloadValue = aggregatePropReloadValue(spi,prop)
        tempDraft.__set(propName, reloadValue)
        return reloadValue
    }

    fun buildProxyDraftFromNoList(draftContext:DraftContext, base: Any, proxyClass: Class<*>? = null): AssociationDraft{
        val spi = base as ImmutableSpi
        val type = spi.__type()
        val propNames = type.props.keys
//        val tempDraft = type.draftFactory.apply(draftContext, base).let { it as DraftSpi }
        val tempDraft = draftContext.toDraftObject<Any>(base).let { it as DraftSpi }
        val changedDraft = type.draftFactory.apply(draftContext, null).let { it as DraftSpi }
        changedDraft.__set(type.idProp.id,tempDraft.__get(type.idProp.id))

        val propertiesHasSetMap = mutableMapOf<String, Boolean>()
        val propertiesLazyHasLoadMap = mutableMapOf<String, Boolean>()

        val proxyClassNotNull = proxyClass?: base::class.java.declaringClass.declaringClass
        val impl = Proxy.newProxyInstance(
            proxyClassNotNull.classLoader,
            arrayOf(proxyClassNotNull,DraftSpi::class.java)
        ) { proxy, method, args ->

            val methodName = method.name
            if (method.parameterCount == 0 && (method.returnType != Void.TYPE && method.returnType != Unit::class.java)) {
                val propName = if (methodName.startsWith("get")) {
                    methodName.substring(3)
                        .replaceFirstChar { it.lowercase() }
                } else if (methodName.startsWith("is")) {
                    methodName.substring(2)
                        .replaceFirstChar { it.lowercase() }
                } else {
                    methodName
                }
                if (propName in propNames) {
                    val prop = type.props[propName]!!
                    val annotations = prop.annotations
                    val lazy = annotations.filterIsInstance<Lazy>().firstOrNull()
                    val hasLoad: Boolean = propertiesLazyHasLoadMap.getOrDefault(propName, false)
                    if (lazy != null && !hasLoad){
                        propertiesLazyHasLoadMap.put(propName, true)
                        return@newProxyInstance reloadAndGetField(prop,tempDraft,changedDraft,spi,propertiesHasSetMap,draftContext)
                    }
                    try {
                        if (prop.isAssociation(TargetLevel.ENTITY)){
                            val aggregatedField = annotations.filterIsInstance<AggregatedField>().firstOrNull()
                            if (aggregatedField == null|| aggregatedField.type == AggregationType.AGGREGATED) {
                                if (!propertiesHasSetMap.getOrDefault(propName, false)) {
                                    val tempDraftValue = tempDraft.__get(propName)
                                    val (proxyAssociationDraft, changedAssociationDraft) = buildProxyDraft(draftContext, tempDraftValue)
                                    propertiesHasSetMap.put(propName,true)
                                    tempDraft.__set(propName, proxyAssociationDraft)
                                    changedDraft.__set(propName, changedAssociationDraft)
                                }
                                val tempDraftValue = tempDraft.__get(propName)
                                val changedDraftValue = changedDraft.__get(propName)
                                if (tempDraftValue is ListDraft<*> && changedDraftValue is ListDraft<*>){
                                    val delegatedMutableList = DelegatedMutableListCache.getOrPut(tempDraftValue, changedDraftValue as ListDraft<Any>)
                                    return@newProxyInstance delegatedMutableList
                                }else{
                                    return@newProxyInstance tempDraft.__get(propName)
                                }
//                                if (tempDraftValue !is DelegatedMutableList<*> && tempDraftValue is ListDraft<*> && changedDraftValue !is DelegatedMutableList<*> && changedDraftValue is ListDraft<*>){
//                                    val listDraftMap: IdentityHashMap<List<*>, ListDraft<*>> = draftContext.getListDraftMap()
//                                    val delegatedMutableList: DelegatedMutableList<*> =
//                                        DelegatedMutableList(tempDraftValue, changedDraftValue as ListDraft<Any>)
//                                    listDraftMap.replaceValuesByReference(tempDraftValue,DelegatedMutableList(tempDraftValue,changedDraftValue as ListDraft<Any>))
//                                    return@newProxyInstance delegatedMutableList
//                                }else{
//                                    return@newProxyInstance tempDraft.__get(propName)
//                                }
                            }else if (aggregatedField.type == AggregationType.ID_ONLY){
                                return@newProxyInstance tempDraft.__get(propName)
                            }else if (aggregatedField.type == AggregationType.NON_AGGREGATED){
                                throw JimmerDDDException("标注不是聚合根的字段，不应该能够加载")
                            }
                        }else{
                            return@newProxyInstance tempDraft.__get(propName)
                        }
                    } catch (e: UnloadedException) {
//                        兜底没加载的字段 再次请求sql去访问
                        log.warn("$proxyClass $propName 字段并没有传入，但却被强制重新从数据库加载了!")
                        return@newProxyInstance reloadAndGetField(prop,tempDraft,changedDraft,spi,propertiesHasSetMap,draftContext)
                    }
                }
            }

            if (method.parameterCount == 1 && (method.returnType == Void.TYPE || method.returnType == Unit::class.java)) {
                if (methodName.startsWith("set")) {
                    val propName = methodName.substring(3)
                        .replaceFirstChar { it.lowercase() }
                    if (propName in propNames) {
                        val prop = type.props[propName]!!
                        val annotations = prop.annotations
                        if (prop.isAssociation(TargetLevel.ENTITY)){
                            val aggregatedField = annotations.filterIsInstance<AggregatedField>().firstOrNull()
                            if (aggregatedField == null|| aggregatedField.type == AggregationType.AGGREGATED) {
                                val value = args[0]
                                val (proxyAssociationDraft, changedAssociationDraft) = buildProxyDraft(draftContext, value)
                                propertiesHasSetMap.put(propName,true)
                                tempDraft.__set(propName, proxyAssociationDraft)
                                return@newProxyInstance changedDraft.__set(propName, changedAssociationDraft)
                            }else if (aggregatedField.type == AggregationType.NON_AGGREGATED){
                                throw JimmerDDDException("标注不是聚合根的字段，不应该能够加载")
                            }
                        }
                        tempDraft.__set(propName, args[0])
                        return@newProxyInstance changedDraft.__set(propName, args[0])
                    }
                }
            }

            // 其他方法直接调用
            return@newProxyInstance method.invoke(changedDraft, *args.orEmpty())
        }
        return AssociationDraft(impl , changedDraft)
    }

    fun buildProxyDraft(draftContext:DraftContext,base: Any): AssociationDraft {
        when (base){
            is ImmutableSpi -> {
                return buildProxyDraftFromNoList(draftContext,base)
            }
            is MutableList<*> -> {
                val newList = base.mapNotNull { item ->
                    buildProxyDraftFromNoList(draftContext,item!!)
                }
                return AssociationDraft(newList.map { it.proxyAssociationDraft }.toMutableList(),newList.map { it.changedAssociationDraft }.toMutableList())
            }
            else -> throw JimmerDDDException("buildProxyDraft ${base::class.simpleName} not supported")
        }
    }
}

data class AssociationDraft(
    val proxyAssociationDraft: Any,
    val changedAssociationDraft: Any,
)