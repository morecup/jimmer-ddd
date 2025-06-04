package org.morecup.jimmerddd.core.aggregateproxy.multi

import org.babyfish.jimmer.runtime.ImmutableSpi


class MultiEntityFactory {
    fun <T> create(entityClass: Class<T>,spiList: List<Any>):T{
        val multiEntityProxy = MultiEntityProxy(MultiPropNameEntityManager(spiList as List<ImmutableSpi>),entityClass)
        return multiEntityProxy.proxy as T
    }
}