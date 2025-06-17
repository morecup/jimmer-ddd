package org.morecup.jimmerddd.betterddd.domain.goods

import org.springframework.stereotype.Service

@Service
class GoodsFactory(
    private val goodsRepository: GoodsRepository
) {
    fun create(cmd: CreateGoodsCmd): Goods  {
        return Goods(cmd.name, cmd.nowAddress)
    }
}