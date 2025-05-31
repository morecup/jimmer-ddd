package org.morecup.jimmerddd.core.aggregateproxy

import org.babyfish.jimmer.runtime.DraftContext
import org.babyfish.jimmer.runtime.DraftSpi
import org.morecup.jimmerddd.core.FindByIdFunction
import org.morecup.jimmerddd.core.JimmerDDDConfig.getEventPublishFunction
import org.morecup.jimmerddd.core.JimmerDDDConfig.publishEvent
import java.lang.reflect.Method
import java.lang.reflect.Proxy

internal open class AggregationProxy(
    private val propNameDraftManager: IPropNameDraftManager,
    private val draftContext: DraftContext,
    private val proxyClass: Class<*>,
    private val findByIdFunction: FindByIdFunction
): EntityProxy(propNameDraftManager, draftContext, proxyClass,findByIdFunction) {
    val lazyPublishEventList = ArrayList<Any>()

    override fun createProxy(): Any {
        return Proxy.newProxyInstance(
            proxyClass.classLoader,
            arrayOf(proxyClass, DraftSpi::class.java,EventHandler::class.java),
            ProxyInvocationHandler()
        )
    }

    override fun handleOtherMethod(proxy: Any, method: Method, args: Array<Any>?): Pair<Boolean,Any?> {
        if (method == publishMethod){
            publishEvent(args!![0])
        }else if (method == lazyPublishMethod){
            lazyPublishEventList.add(args!![0])
        }
        return false to null
    }
}

interface EventHandler {
    fun publishEvent(event: Any)

    fun lazyPublishEvent(event: Any)
}

private val publishMethod = EventHandler::class.java.getMethod("publishEvent", Any::class.java);
private val lazyPublishMethod = EventHandler::class.java.getMethod("lazyPublishEvent", Any::class.java);