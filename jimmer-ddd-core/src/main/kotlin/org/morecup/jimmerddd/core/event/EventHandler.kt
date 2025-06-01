package org.morecup.jimmerddd.core.event

interface EventHandler {
    fun publishEvent(event: Any)

    fun lazyPublishEvent(event: Any)
}