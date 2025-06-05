@file:JvmName("FetcherImplExtension")
package org.morecup.jimmerddd.core.aggregateproxy

import org.babyfish.jimmer.sql.fetcher.impl.FetcherImpl
import org.babyfish.jimmer.sql.fetcher.impl.FetcherImplementor
import org.morecup.jimmerddd.core.annotation.AggregatedField
import org.morecup.jimmerddd.core.annotation.AggregationType

@JvmOverloads
fun <E> FetcherImpl<E>.allAggregationFields(loadedClassList:List<Class<*>> = arrayListOf()):FetcherImplementor<E> {
    var fetcher:FetcherImplementor<E> = this
    for (prop in immutableType.props.values) {

        val aggregatedField:AggregatedField? = prop.getAnnotation(AggregatedField::class.java)
        val notNullAggregatedField:AggregatedField = aggregatedField?:AggregatedField(type = AggregationType.DEFAULT)
        if (notNullAggregatedField.lazy) {
            continue
        }
        if ((prop.isTransient || prop.isFormula)&&notNullAggregatedField.type == AggregationType.DEFAULT){
            continue
        }
        if (notNullAggregatedField.type == AggregationType.DEFAULT|| notNullAggregatedField.type == AggregationType.AGGREGATED) {
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
        }else if (notNullAggregatedField.type == AggregationType.ID_ONLY){
            fetcher = fetcher.add(prop.name)
        }else if (notNullAggregatedField.type == AggregationType.NON_AGGREGATED){
            continue
        }
    }
    return fetcher
}