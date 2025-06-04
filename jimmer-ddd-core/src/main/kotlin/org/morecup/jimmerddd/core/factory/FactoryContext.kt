@file:JvmName("FactoryContext")
package org.morecup.jimmerddd.core.factory

import org.morecup.jimmerddd.core.JimmerDDDConfig
import org.morecup.jimmerddd.core.aggregateproxy.nullDraftContext
import org.morecup.jimmerddd.core.event.EventHandler

/**
 * 自动在结束调用时发布事件并且保存实体
 * @return 保存后的modifiedEntity
 */
fun <T> autoContext(block: EventHandler.()->T): T = nullDraftContext {
    val (entity,lazyEventList)=doEventBlock(block)
    val modifiedEntity = entity?.let { JimmerDDDConfig.getSaveEntityFunction().invoke(it) }
    lazyEventList.forEach {
        JimmerDDDConfig.getEventPublishFunction().publish(it)
    }
    modifiedEntity as T
}

/**
 * 只添加事件上下文，并不自动发布事件和保存实体
 */
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

/**
 * 自动保存实体并返回保存后的modifiedEntity
 */
fun <T> saveAutoContext(block: EventHandler.()->T): FactoryModifiedResult<T> = nullDraftContext {
    val (entity,lazyEventList)=doEventBlock(block)
    val modifiedEntity = JimmerDDDConfig.getSaveEntityFunction().invoke(entity!!) as T
    FactoryModifiedResult(modifiedEntity,lazyEventList)
}

/**
 * 保留相同的上下文，不自动发布事件和保存实体，需要用于在另一个方法里保留同一个上下文，发布事件
 */
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
    /**
     * 注意是保存后的实体，不需要再次保存
     */
    val modifiedEntity: T,
    val lazyEventList: List<Any>
)