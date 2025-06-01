package org.morecup.jimmerddd.core.factory

import org.morecup.jimmerddd.core.JimmerDDDConfig
import org.morecup.jimmerddd.core.aggregateproxy.EventHandler
import org.morecup.jimmerddd.core.aggregateproxy.GlobalContext.nullDraftContext

object FactoryContext {
    @JvmStatic
    fun <T> autoContext(block: EventHandler.()->T): T = nullDraftContext {
        val lazyEventList = mutableListOf<Any>()
        val entity = object : EventHandler{
            override fun publishEvent(event: Any) {
                JimmerDDDConfig.getEventPublishFunction().publish(event)
            }
            override fun lazyPublishEvent(event: Any) {
                lazyEventList.add(event)
            }
        }.let(block)
        val modifiedEntity = entity?.let { JimmerDDDConfig.getSaveEntityFunction().invoke(it) }
        lazyEventList.forEach {
            JimmerDDDConfig.getEventPublishFunction().publish(it)
        }
        modifiedEntity as T
    }

    @JvmStatic
    fun <T> eventContext(block: EventHandler.()->T): FactoryResult<T> = nullDraftContext {
        val lazyEventList = mutableListOf<Any>()
        val entity = object : EventHandler{
            override fun publishEvent(event: Any) {
                JimmerDDDConfig.getEventPublishFunction().publish(event)
            }
            override fun lazyPublishEvent(event: Any) {
                lazyEventList.add(event)
            }
        }.let(block)
        FactoryResult(entity,lazyEventList)
    }
}

data class FactoryResult<T>(
    val entity: T,
    val lazyEventList: List<Any>
)
