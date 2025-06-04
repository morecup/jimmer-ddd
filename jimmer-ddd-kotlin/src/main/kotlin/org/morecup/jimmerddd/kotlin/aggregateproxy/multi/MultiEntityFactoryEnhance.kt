package org.morecup.jimmerddd.kotlin.aggregateproxy.multi

import org.morecup.jimmerddd.core.aggregateproxy.multi.MultiEntityFactory
import kotlin.reflect.KClass

/**
 * 相同的属性，后面的会覆盖前面的
 */
fun <T : Any> MultiEntityFactory.create(entityClass: KClass<T>,spiList: List<Any>):T{
    return create(entityClass.java,spiList)
}

/**
 * 相同的属性，后面的会覆盖前面的
 */
fun <T : Any> MultiEntityFactory.create(entityClass: KClass<T>, vararg spiList: Any):T{
    return create(entityClass.java,spiList.toList())
}
