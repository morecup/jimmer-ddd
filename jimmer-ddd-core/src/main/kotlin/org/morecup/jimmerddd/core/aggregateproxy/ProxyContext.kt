package org.morecup.jimmerddd.core.aggregateproxy

import org.babyfish.jimmer.runtime.DraftContext
import org.babyfish.jimmer.runtime.ImmutableSpi
import org.morecup.jimmerddd.core.FindByIdFunction
import org.morecup.jimmerddd.core.aggregateproxy.GlobalContext.usingDraftContext

internal class ProxyContext<T,P>(
    private val base: T,
    private val implInterfaceClass: Class<*>,
    private val findByIdFunction: FindByIdFunction
) {
    private val spiBase = base as ImmutableSpi
    private val draftContext = DraftContext(null)
    private val singleSpiPropNameManager = SingleSpiPropNameManager(spiBase,draftContext)
    private val entityProxy = AggregationProxy(singleSpiPropNameManager, draftContext, implInterfaceClass, findByIdFunction)

    fun <R> execute(processor: (P) -> R): Pair<T, R> {
        val proxy = entityProxy.proxy as P
        val result = usingDraftContext(draftContext){
            processor(proxy)
        }
        val changed = singleSpiPropNameManager.changedDraft.__resolve()
        draftContext.dispose()
        return changed as T to result
    }
}