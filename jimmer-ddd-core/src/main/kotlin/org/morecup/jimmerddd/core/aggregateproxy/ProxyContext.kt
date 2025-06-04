package org.morecup.jimmerddd.core.aggregateproxy

import org.babyfish.jimmer.runtime.DraftContext
import org.babyfish.jimmer.runtime.ImmutableSpi
import org.morecup.jimmerddd.core.FindByIdFunction
import org.morecup.jimmerddd.core.aggregateproxy.usingDraftContext

internal class ProxyContext<T,P>(
    private val base: T,
    private val implInterfaceClass: Class<*>,
    private val findByIdFunction: FindByIdFunction
) {
    private val spiBase = base as ImmutableSpi
    private val draftContext = DraftContext(null)
    private val singleSpiPropNameManager = SingleSpiPropNameManager(spiBase,draftContext)
    private val aggregationProxy = AggregationProxy(singleSpiPropNameManager, draftContext, implInterfaceClass, findByIdFunction)

    fun <R> execute(processor: (P) -> R): ProxyResult<T, R> {
        val proxy = aggregationProxy.proxy as P
        val result = usingDraftContext(draftContext){
            processor(proxy)
        }
        val changed = singleSpiPropNameManager.changedDraft.__resolve()
        draftContext.dispose()
        return ProxyResult(changed as T , result , aggregationProxy.lazyPublishEventList)
    }
}
data class ProxyResult<T,P>(
    /**
     * 需要保存的实体，该实体只包含被修改的属性，其他属性为unload
     */
    val changed: T,
    /**
     * lambda执行结果
     */
    val result: P,
    /**
     * 需要懒加载发布的事件列表（聚合内lazyPublishEvent的事件不会立刻发布，会直接返回成列表）
     */
    val lazyPublishEventList: List<Any>
)