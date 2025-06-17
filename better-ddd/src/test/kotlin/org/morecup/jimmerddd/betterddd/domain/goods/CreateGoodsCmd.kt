package org.morecup.jimmerddd.betterddd.domain.goods

import org.springframework.stereotype.Service

data class CreateGoodsCmd(
    val name: String,
    val nowAddress: String
)

@Service
class CreateGoodsCmdHandle(
    private val goodsRepository: GoodsRepository,
    private val goodsFactory: GoodsFactory,
) {
    fun handle(command: CreateGoodsCmd): Long {
        val goods = goodsFactory.create(command)
        val updatedGoods = goodsRepository.saveGoods(goods)
        return updatedGoods.id!!
    }
}