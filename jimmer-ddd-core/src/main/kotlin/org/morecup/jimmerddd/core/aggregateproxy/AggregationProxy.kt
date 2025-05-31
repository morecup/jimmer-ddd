package org.morecup.jimmerddd.core.aggregateproxy

import org.babyfish.jimmer.runtime.DraftContext
import org.babyfish.jimmer.runtime.DraftSpi
import org.morecup.jimmerddd.core.FindByIdFunction
import java.lang.reflect.Method
import java.lang.reflect.Proxy

internal open class AggregationProxy(
    private val propNameDraftManager: IPropNameDraftManager,
    private val draftContext: DraftContext,
    private val proxyClass: Class<*>,
    private val findByIdFunction: FindByIdFunction
): EntityProxy(propNameDraftManager, draftContext, proxyClass,findByIdFunction) {
    override fun createProxy(): Any {
        return Proxy.newProxyInstance(
            proxyClass.classLoader,
            arrayOf(proxyClass, DraftSpi::class.java),
            ProxyInvocationHandler()
        )
    }

    override fun handleOtherMethod(proxy: Any, method: Method, args: Array<Any>?): Pair<Boolean,Any?> {
        return false to null
    }
}