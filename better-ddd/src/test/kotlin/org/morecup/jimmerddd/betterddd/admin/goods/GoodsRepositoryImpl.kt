package org.morecup.jimmerddd.betterddd.admin.goods

import org.babyfish.jimmer.sql.kt.KSqlClient
import org.morecup.jimmerddd.betterddd.domain.goods.Goods
import org.morecup.jimmerddd.betterddd.domain.goods.GoodsRepository
import org.springframework.stereotype.Repository

@Repository
open class GoodsRepositoryImpl(
    private val kSqlClient: KSqlClient
): GoodsRepository {
    override fun saveGoods(goods: Goods): Goods {
        return kSqlClient.save(goods).modifiedEntity
    }

    override fun findByIdOrErr(id: Long): Goods {
        val goods: Goods? = kSqlClient.findById(Goods::class, id) ?: throw RuntimeException("Goods not found")
//        goods-> proxy-> domain goods
        return goods!!
    }


}