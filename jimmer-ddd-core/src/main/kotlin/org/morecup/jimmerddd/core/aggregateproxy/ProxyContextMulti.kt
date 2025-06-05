package org.morecup.jimmerddd.core.aggregateproxy

import org.babyfish.jimmer.runtime.DraftContext
import org.babyfish.jimmer.runtime.ImmutableSpi
import org.morecup.jimmerddd.core.FindByIdFunction
import org.morecup.jimmerddd.core.aggregateproxy.multi.MultiEntity
import org.morecup.jimmerddd.core.aggregateproxy.multi.MultiEntityFactory
import org.morecup.jimmerddd.core.aggregateproxy.multi.MultiEntityProxy

internal class ProxyContextMulti<P,T : MultiEntity>(
    private val multiEntity: T,
    private val implInterfaceClass: Class<*>,
    private val findByIdFunction: FindByIdFunction
) {
    private val spiBases = multiEntity.toEntityList().map { it as ImmutableSpi }
    private val draftContext = DraftContext(null)
    private val multiSpiPropNameManager = MultiSpiPropNameManager(spiBases,draftContext)
    private val aggregationProxy = AggregationProxy(multiSpiPropNameManager, draftContext, implInterfaceClass, findByIdFunction)

    fun <R> execute(processor: (P) -> R): MultiProxyResult<R, T> {
        val proxy = aggregationProxy.proxy as P
        val result = usingDraftContext(draftContext){
            processor(proxy)
        }
        val changed = multiSpiPropNameManager.changedDraftList.map { it.__resolve()  }
        draftContext.dispose()
        return MultiProxyResult(MultiEntityFactory.create(multiEntity.entityClass() as Class<T>,changed) , result , aggregationProxy.lazyPublishEventList)
    }
}

data class MultiProxyResult<P,T: MultiEntity>(
    val changed: T,
    val result: P,
    val lazyPublishEventList: List<Any>
)