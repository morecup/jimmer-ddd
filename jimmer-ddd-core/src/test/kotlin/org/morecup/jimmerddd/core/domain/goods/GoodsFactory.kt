package org.morecup.jimmerddd.core.domain.goods

import org.morecup.jimmerddd.core.domain.goods.dto.CreateGoodsCmd
import org.springframework.stereotype.Service

@Service
class GoodsFactory(
    private val goodsRepository: GoodsRepository
) {
    fun create(cmd: CreateGoodsCmd): Goods {
        return cmd.toEntity()
    }
}