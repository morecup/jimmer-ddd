package org.morecup.jimmerddd.kotlin.aggregateproxy.multi

import org.morecup.jimmerddd.core.aggregateproxy.multi.MultiEntityFactory
import kotlin.reflect.KClass


fun <T : Any> MultiEntityFactory.create(entityClass: KClass<T>,spiList: List<Any>):T{
    return create(entityClass.java,spiList)
}

fun <T : Any> MultiEntityFactory.create(entityClass: KClass<T>, vararg spiList: Any):T{
    return create(entityClass.java,spiList.toList())
}
