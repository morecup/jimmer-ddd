package org.morecup.jimmerddd.core.event

import org.babyfish.jimmer.Draft
import org.morecup.jimmerddd.core.JimmerDDDConfig
import org.morecup.jimmerddd.core.JimmerDDDException
import org.morecup.jimmerddd.core.aggregateproxy.nullDraftContext

object EventManager {
    @JvmStatic
    fun publish(event: Any) {
        nullDraftContext {
            JimmerDDDConfig.getEventPublishFunction().publish(event)
        }
    }

    @JvmStatic
    fun publish(event: List<Any>) {
        nullDraftContext {
            event.forEach { value -> JimmerDDDConfig.getEventPublishFunction().publish(value) }
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

/**
 * 延迟事件，在实体save后才会发布(需要走框架的save方法或使用autoSave)
 */
fun Draft.lazyPublishEvent(event: Any) {
    if (this is EventHandler) {
        this.lazyPublishEvent(event)
    }else{
        throw JimmerDDDException("错误的调用，当前draft对象并没有实现EventHandler接口，注意，聚合内不能直接调用lazyPublishEvent方法，应该委派EventHandler（Kotlin）或者继承publishEvent方法(Java)")
    }
}