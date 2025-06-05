package org.morecup.jimmerddd.core.aggregateproxy

import org.babyfish.jimmer.runtime.ImmutableSpi

fun isIdLoaded(base: Any): Boolean{
    val immutable = base
    val spi = immutable as ImmutableSpi
    val type = spi.__type()
    return spi.__isLoaded(type.idProp.id)
}