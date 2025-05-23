package org.morecup.jimmerddd.core.domain.goods

import org.morecup.jimmerddd.core.aggregateproxy.GlobalContext.nullDraftContext
import org.morecup.jimmerddd.core.domain.order.RenameEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class RenameGoodsNameWhenOrderRename(
    val goodsRepository: GoodsRepository
) {
//    不加也可以，如果是工厂模式，则必须加
    @EventListener
    fun onEvent(event: RenameEvent) = nullDraftContext{
        val goods: Goods = goodsRepository.findByIdOrErr(event.goodsId)
        goodsAggregateProxy.exec(goods) { draft ->
            GoodsImpl(draft).rename(event.newName)
        }
        goodsRepository.saveGoods(goods)
    }
}