package org.morecup.jimmerddd.betterddd.jimmer.admin.goods

import org.babyfish.jimmer.runtime.DraftContext
import org.babyfish.jimmer.runtime.DraftSpi
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.morecup.jimmerddd.betterddd.core.proxy.DomainAggregateRoot
import org.morecup.jimmerddd.betterddd.jimmer.domain.goods.Goods
import org.morecup.jimmerddd.betterddd.jimmer.domain.goods.GoodsRepository
import org.springframework.stereotype.Repository

@Repository
open class GoodsRepositoryImpl(
    private val kSqlClient: KSqlClient
): GoodsRepository {
    override fun saveGoods(goods: Goods) {
        val tempDraft = DomainAggregateRoot.findArgs(goods)[0] as DraftSpi
        val changed = tempDraft.__resolve()
        kSqlClient.save(changed)
        tempDraft.__draftContext().dispose()
        return
    }

    override fun findByIdOrErr(id: Long): Goods {
        val goodsEntity: GoodsEntity = kSqlClient.findById(GoodsEntity::class, id) ?: throw RuntimeException("Goods not found")
//        goods-> proxy-> domain goods
        val tempDraft = DraftContext(null).toDraftObject<Any>(goodsEntity).let { it as DraftSpi }
        val goods: Goods = DomainAggregateRoot.build(Goods::class.java, tempDraft)
        return goods
    }


}