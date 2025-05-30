package org.morecup.jimmerddd.core.aggregateproxy

import org.babyfish.jimmer.runtime.DraftContext
import org.babyfish.jimmer.runtime.ImmutableSpi
import org.morecup.jimmerddd.core.FindByIdFunction
import org.morecup.jimmerddd.core.aggregateproxy.GlobalContext.usingDraftContext

class ProxyContext<T,P>(
    private val base: T,
    private val implInterfaceClass: Class<*>,
    private val findByIdFunction: FindByIdFunction
) {
    private val spiBase = base as ImmutableSpi
    private val draftContext = DraftContext(null)
    private val draftChangeProxy = DraftChangeProxy(spiBase, draftContext, implInterfaceClass, findByIdFunction)

    fun <R> execute(processor: (P) -> R): Pair<T, R> {
        val proxy = draftChangeProxy.proxy as P
        val result = usingDraftContext(draftContext){
            processor(proxy)
        }
        val changed = draftChangeProxy.changedDraft.__resolve()
        draftContext.dispose()
        return changed as T to result
    }
}