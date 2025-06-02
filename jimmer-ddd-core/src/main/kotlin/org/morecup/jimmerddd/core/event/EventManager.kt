package org.morecup.jimmerddd.core.event

import org.babyfish.jimmer.Draft
import org.morecup.jimmerddd.core.JimmerDDDConfig
import org.morecup.jimmerddd.core.aggregateproxy.nullDraftContext

object EventManager {
    @JvmStatic
    fun publish(event: Any) {
        nullDraftContext {
            JimmerDDDConfig.getEventPublishFunction().publish(event)
        }
    }

    @JvmStatic
    fun publishWithLocalDraftContext(event: Any) {
        JimmerDDDConfig.getEventPublishFunction().publish(event)
    }
}

fun Draft.publishEvent(event: Any) {
    EventManager.publish(event)
}

fun Draft.lazyPublishEvent(event: Any) {
    if (this is EventHandler) {
        this.lazyPublishEvent(event)
    }
}