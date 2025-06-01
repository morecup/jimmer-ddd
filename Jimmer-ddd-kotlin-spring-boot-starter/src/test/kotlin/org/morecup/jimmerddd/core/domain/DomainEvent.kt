package org.morecup.jimmerddd.core.domain

import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationEventPublisher

open class DomainEvent(source:Any): ApplicationEvent(source){
    fun publish(){
        DomainRegistry.spring.publishEvent(this)
    }
}