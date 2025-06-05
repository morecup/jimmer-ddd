package org.morecup.jimmerddd.core.aggregateproxy

import org.babyfish.jimmer.runtime.DraftContext
import org.babyfish.jimmer.runtime.DraftSpi
import org.morecup.jimmerddd.core.FindByIdFunction
import org.morecup.jimmerddd.core.event.EventHandler
import org.morecup.jimmerddd.core.event.EventManager.publish
import java.lang.reflect.Method

internal open class AggregationProxy(
    private val propNameDraftManager: IPropNameDraftManager,
    private val draftContext: DraftContext,
    private val proxyClass: Class<*>,
    private val findByIdFunction: FindByIdFunction
): EntityProxy(propNameDraftManager, draftContext, proxyClass,findByIdFunction) {
    val lazyPublishEventList = ArrayList<Any>()

    override fun createProxy(): Any {
        return byteBuddyNewProxyInstance(
            proxyClass.classLoader,
            listOf(proxyClass, DraftSpi::class.java,EventHandler::class.java),
            ProxyInvocationHandler()
        )
    }

    override fun handleOtherMethod(proxy: Any, method: Method, args: Array<Any>?): Pair<Boolean,Any?> {
        if (method == publishMethod){
            publish(args!![0])
        }else if (method == lazyPublishMethod){
            lazyPublishEventList.add(args!![0])
        }
        return true to null
    }
}

private val publishMethod = EventHandler::class.java.getMethod("publishEvent", Any::class.java);
private val lazyPublishMethod = EventHandler::class.java.getMethod("lazyPublishEvent", Any::class.java);