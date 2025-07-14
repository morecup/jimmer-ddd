package org.morecup.jimmerddd.betterddd.jimmer.domain.goods

import org.morecup.jimmerddd.betterddd.jimmer.domain.goods.Goods

interface GoodsRepository {
    /**
     * 持久化Goods
     * @param goods 要保存的实体（按引用传递）
     */
    fun saveGoods(goods: Goods)

    /**
     * 查找并校验存在性
     * @param id 实体标识符
     */
    fun findByIdOrErr(id: Long): Goods
}