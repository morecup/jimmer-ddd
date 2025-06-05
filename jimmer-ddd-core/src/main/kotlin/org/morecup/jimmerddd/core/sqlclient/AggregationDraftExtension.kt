@file:JvmName("ImmutableSpiExtend")
package org.morecup.jimmerddd.core.sqlclient

import org.babyfish.jimmer.runtime.ImmutableSpi

fun checkIsInsertOrUpdate(base: Any): Boolean {
    if (base is AggregationEntity){
        return base.checkIsInsertOrUpdate()
    }
    val spi = base as ImmutableSpi
    val type = spi.__type()
    return if (!spi.__isLoaded(type.idProp.id)){
        true
    }else if (type.props.containsKey("idPreLoaded")&&spi.__isLoaded("idPreLoaded")&& spi.__get("idPreLoaded") as Boolean){
        true
    }else{
        false
    }
}