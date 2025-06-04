package org.morecup.jimmerddd.core.aggregateproxy.multi

import org.babyfish.jimmer.runtime.ImmutableSpi


object MultiEntityFactory {
    @JvmStatic
    fun <T> create(entityClass: Class<T>,spiList: List<Any>):T{
        val multiEntityProxy = MultiEntityProxy(MultiPropNameEntityManager(spiList as List<ImmutableSpi>),entityClass)
        return multiEntityProxy.proxy as T
    }

    @JvmStatic
    fun <T> create(entityClass: Class<T>,vararg spiList: Any):T{
        val multiEntityProxy = MultiEntityProxy(MultiPropNameEntityManager(spiList.toList() as List<ImmutableSpi>),entityClass)
        return multiEntityProxy.proxy as T
    }
}