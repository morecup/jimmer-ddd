package org.morecup.jimmerddd.core.aggregateproxy.multi

interface MultiEntity {
    fun getEntityList(): List<Any>
}