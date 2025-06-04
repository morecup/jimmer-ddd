package org.morecup.jimmerddd.core.aggregateproxy.multi

interface MultiEntity {
    fun toEntityList(): List<Any>
}