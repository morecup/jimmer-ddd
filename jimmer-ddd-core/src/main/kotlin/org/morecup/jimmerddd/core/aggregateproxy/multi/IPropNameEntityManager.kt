package org.morecup.jimmerddd.core.aggregateproxy.multi


interface IPropNameEntityManager {
    fun contains(propName: String): Boolean
    fun getEntityPropValue(propName: String): Any?

    val proxyDefaultEntity: Any
    val entityList: List<Any>
}