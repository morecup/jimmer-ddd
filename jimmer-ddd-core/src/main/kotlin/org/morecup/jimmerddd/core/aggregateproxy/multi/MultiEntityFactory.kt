package org.morecup.jimmerddd.core.aggregateproxy.multi

import org.babyfish.jimmer.runtime.ImmutableSpi
import kotlin.jvm.java
import kotlin.reflect.KClass


object MultiEntityFactory {
    /**
     * 相同的属性，后面的会覆盖前面的
     */
    @JvmStatic
    fun <T> create(entityClass: Class<T>,spiList: List<Any>):T{
        val multiEntityProxy = MultiEntityProxy(MultiPropNameEntityManager(spiList as List<ImmutableSpi>),entityClass)
        return multiEntityProxy.proxy as T
    }

    /**
     * 相同的属性，后面的会覆盖前面的
     */
    @JvmStatic
    fun <T> create(entityClass: Class<T>,vararg spiList: Any):T{
        val multiEntityProxy = MultiEntityProxy(MultiPropNameEntityManager(spiList.toList() as List<ImmutableSpi>),entityClass)
        return multiEntityProxy.proxy as T
    }

    fun <T : Any> create(entityClass: KClass<T>, spiList: List<ImmutableSpi>):T{
        return create(entityClass.java,spiList)
    }

    fun <T : Any> create(entityClass: KClass<T>, vararg spiList: ImmutableSpi):T{
        return create(entityClass.java,spiList.toList())
    }
}