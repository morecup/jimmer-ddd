package org.morecup.jimmerddd.core.aggregateproxy

import org.babyfish.jimmer.runtime.DraftContext
import org.babyfish.jimmer.runtime.ImmutableSpi
import org.morecup.jimmerddd.core.FindByIdFunction
import org.morecup.jimmerddd.core.aggregateproxy.GlobalContext.usingDraftContext

internal class ProxyContextMulti<P>(
    private val bases: List<Any>,
    private val implInterfaceClass: Class<*>,
    private val findByIdFunction: FindByIdFunction
) {
    private val spiBases = bases.map { it as ImmutableSpi }
    private val draftContext = DraftContext(null)
    private val multiSpiPropNameManager = MultiSpiPropNameManager(spiBases,draftContext)
    private val entityProxy = AggregationProxy(multiSpiPropNameManager, draftContext, implInterfaceClass, findByIdFunction)

    fun <R> execute(processor: (P) -> R): Pair<List<Any>, R> {
        val proxy = entityProxy.proxy as P
        val result = usingDraftContext(draftContext){
            processor(proxy)
        }
        val changed = multiSpiPropNameManager.changedDraftList.map { it.__resolve()  }
        draftContext.dispose()
        return changed as List<Any> to result
    }
}