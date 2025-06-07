@file:JvmName("ImmutableSpiExtension")
package org.morecup.jimmerddd.core.aggregateproxy

import org.babyfish.jimmer.meta.TargetLevel
import org.babyfish.jimmer.runtime.ImmutableSpi

fun isIdLoaded(base: Any): Boolean{
    val immutable = base
    val spi = immutable as ImmutableSpi
    val type = spi.__type()
    return spi.__isLoaded(type.idProp.id)
}

fun isIdOnlyIgnoreAssociation(base: Any): Boolean{
    val immutable = base
    val spi = immutable as ImmutableSpi
    val type = spi.__type()
    for (prop in type.props.values) {
        if (prop.isId){
            if (!spi.__isLoaded(prop.id)){
                return false
            }
        }else if (spi.__isLoaded(prop.getId())&&!prop.isAssociation(TargetLevel.ENTITY)&&!prop.isTransient &&!prop.isFormula){
            return false
        }
    }
    return true
}