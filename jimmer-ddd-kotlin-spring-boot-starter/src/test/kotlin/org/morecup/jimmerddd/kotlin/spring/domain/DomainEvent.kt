package org.morecup.jimmerddd.kotlin.spring.domain

import org.springframework.context.ApplicationEvent

open class DomainEvent(source:Any): ApplicationEvent(source){
    fun publish(){
        DomainRegistry.spring.publishEvent(this)
    }
}