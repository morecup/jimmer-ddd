@file:JvmName("FactoryContext")
package org.morecup.jimmerddd.core.factory

import org.morecup.jimmerddd.core.JimmerDDDConfig
import org.morecup.jimmerddd.core.aggregateproxy.nullDraftContext
import org.morecup.jimmerddd.core.event.EventHandler


fun <T> autoContext(block: EventHandler.()->T): T = nullDraftContext {
    val (entity,lazyEventList)=doEventBlock(block)
    val modifiedEntity = entity?.let { JimmerDDDConfig.getSaveEntityFunction().invoke(it) }
    lazyEventList.forEach {
        JimmerDDDConfig.getEventPublishFunction().publish(it)
    }
    modifiedEntity as T
}

fun <T> eventContext(block: EventHandler.()->T): FactoryResult<T> = nullDraftContext {
    doEventBlock(block)
}

/**
 * 自动在结束调用时发布事件
 */
fun <T> eventAutoContext(block: EventHandler.()->T): T = nullDraftContext {
    val (entity,lazyEventList)=doEventBlock(block)
    lazyEventList.forEach {
        JimmerDDDConfig.getEventPublishFunction().publish(it)
    }
    entity
}

fun <T> saveAutoContext(block: EventHandler.()->T): FactoryModifiedResult<T> = nullDraftContext {
    val (entity,lazyEventList)=doEventBlock(block)
    val modifiedEntity = JimmerDDDConfig.getSaveEntityFunction().invoke(entity!!) as T
    FactoryModifiedResult(modifiedEntity,lazyEventList)
}

fun <T> sameContext(eventHandler: EventHandler,block: EventHandler.()->T): T = nullDraftContext {
    block.invoke(eventHandler)
}

private fun <T> doEventBlock(block: EventHandler.()->T): FactoryResult<T>{
    val lazyEventList = mutableListOf<Any>()
    val entity = object : EventHandler{
        override fun publishEvent(event: Any) {
            JimmerDDDConfig.getEventPublishFunction().publish(event)
        }
        override fun lazyPublishEvent(event: Any) {
            lazyEventList.add(event)
        }
    }.let(block)
    return FactoryResult(entity,lazyEventList)
}


data class FactoryResult<T>(
    val entity: T,
    val lazyEventList: List<Any>
)
data class FactoryModifiedResult<T>(
    val modifiedEntity: T,
    val lazyEventList: List<Any>
)