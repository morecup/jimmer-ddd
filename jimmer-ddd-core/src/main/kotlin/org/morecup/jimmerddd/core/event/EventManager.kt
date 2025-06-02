package org.morecup.jimmerddd.core.event

import org.babyfish.jimmer.Draft
import org.morecup.jimmerddd.core.JimmerDDDConfig
import org.morecup.jimmerddd.core.aggregateproxy.GlobalContext.nullDraftContext

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
    EventManager.publishWithLocalDraftContext(event)
}

fun Draft.lazyPublishEvent(event: Any) {
    EventManager.publish(event)
}