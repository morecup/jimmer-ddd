package org.morecup.jimmerddd.kotlin.spring.domain.goods

import org.springframework.stereotype.Service

@Service
class GoodsFactory(
    private val goodsRepository: GoodsRepository
) {
    fun create(cmd: CreateGoodsCmd): Goods {
        return Goods {
            name = cmd.name
            nowAddress = cmd.nowAddress
        }
    }
}