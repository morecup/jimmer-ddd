package org.morecup.jimmerddd.kotlin.spring.event

import org.morecup.jimmerddd.core.event.EventManager
import org.springframework.context.ApplicationEvent

fun ApplicationEvent.publish() {
    EventManager.publish(this)
}

fun ApplicationEvent.publishWithDraftContext() {
    EventManager.publishWithLocalDraftContext(this)
}