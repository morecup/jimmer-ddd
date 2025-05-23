package org.morecup.jimmerddd.core.domain.goods

import org.morecup.jimmerddd.core.domain.goods.dto.CreateGoodsCmd
import org.springframework.stereotype.Service

@Service
class CreateGoodsCmdHandle(
    private val goodsRepository: GoodsRepository,
    private val goodsFactory: GoodsFactory,
) {
    fun handle(command: CreateGoodsCmd): Long {
        val goods = goodsFactory.create(command)
        val updatedGoods = goodsRepository.saveGoods(goods)
        return updatedGoods.id
    }
}