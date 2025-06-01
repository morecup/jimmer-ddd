package org.morecup.jimmerddd.core.event

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
    fun publishWithDraftContext(event: Any) {
        JimmerDDDConfig.getEventPublishFunction().publish(event)
    }
}