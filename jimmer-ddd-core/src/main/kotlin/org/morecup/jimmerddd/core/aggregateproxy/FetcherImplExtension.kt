package org.morecup.jimmerddd.core.aggregateproxy

import org.babyfish.jimmer.sql.fetcher.impl.FetcherImpl
import org.babyfish.jimmer.sql.fetcher.impl.FetcherImplementor
import org.morecup.jimmerddd.core.annotation.AggregatedField
import org.morecup.jimmerddd.core.annotation.AggregationType
import org.morecup.jimmerddd.core.annotation.Lazy
import org.morecup.jimmerddd.core.annotation.PrepareLoadMode

fun <E> FetcherImpl<E>.allAggregationFields(loadedClassList:List<Class<*>> = arrayListOf()):FetcherImplementor<E> {
    var fetcher:FetcherImplementor<E> = this
    for (prop in immutableType.props.values) {
        if (prop.isTransient && !prop.hasTransientResolver()) {
            continue
        }
        val annotations = prop.annotations
        val aggregatedField = annotations.filterIsInstance<AggregatedField>().firstOrNull()
        val lazy = annotations.filterIsInstance<Lazy>().firstOrNull()
        if (lazy != null && lazy.prepareLoadMode == PrepareLoadMode.Unload) {
            continue
        }
        if (aggregatedField == null|| aggregatedField.type == AggregationType.AGGREGATED) {
            if (prop.targetType!=null){
                if (!loadedClassList.contains(prop.targetType.javaClass)){
                    val fields = FetcherImpl(prop.targetType.javaClass).allAggregationFields(loadedClassList+immutableType.javaClass)
                    fetcher = fetcher.add(prop.name,fields)
                }else{
                    continue
                }
            }else{
                fetcher = fetcher.add(prop.name)
            }
        }else if (aggregatedField.type == AggregationType.ID_ONLY){
            fetcher = fetcher.add(prop.name)
        }else if (aggregatedField.type == AggregationType.NON_AGGREGATED){
            continue
        }
    }
    return fetcher
}