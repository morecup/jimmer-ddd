package org.morecup.jimmerddd.core.aggregateproxy

import org.babyfish.jimmer.runtime.DraftContext
import org.babyfish.jimmer.runtime.ImmutableSpi
import org.morecup.jimmerddd.core.FindByIdFunction

internal class ProxyContextMulti<P>(
    private val bases: List<Any>,
    private val implInterfaceClass: Class<*>,
    private val findByIdFunction: FindByIdFunction
) {
    private val spiBases = bases.map { it as ImmutableSpi }
    private val draftContext = DraftContext(null)
    private val multiSpiPropNameManager = MultiSpiPropNameManager(spiBases,draftContext)
    private val aggregationProxy = AggregationProxy(multiSpiPropNameManager, draftContext, implInterfaceClass, findByIdFunction)

    fun <R> execute(processor: (P) -> R): MultiProxyResult<R> {
        val proxy = aggregationProxy.proxy as P
        val result = usingDraftContext(draftContext){
            processor(proxy)
        }
        val changed = multiSpiPropNameManager.changedDraftList.map { it.__resolve()  }
        draftContext.dispose()
        return MultiProxyResult(changed as List<Any> , result , aggregationProxy.lazyPublishEventList)
    }
}
data class MultiProxyResult<P>(
    val changed: List<Any>,
    val result: P,
    val lazyPublishEventList: List<Any>
)