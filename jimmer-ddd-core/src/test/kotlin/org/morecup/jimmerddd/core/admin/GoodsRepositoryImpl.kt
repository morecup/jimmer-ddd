package org.morecup.jimmerddd.core.admin

import org.babyfish.jimmer.spring.repo.support.AbstractKotlinRepository
import org.babyfish.jimmer.sql.ast.mutation.AssociatedSaveMode
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.morecup.jimmerddd.core.domain.goods.Goods
import org.morecup.jimmerddd.core.domain.goods.GoodsRepository
import org.springframework.stereotype.Repository

@Repository
open class GoodsRepositoryImpl(
    sql: KSqlClient
) : AbstractKotlinRepository<Goods, Long>(sql), GoodsRepository {

    override fun saveGoods(goods: Goods): Goods {
        return save(goods, SaveMode.NON_IDEMPOTENT_UPSERT, AssociatedSaveMode.MERGE).modifiedEntity
    }

    override fun findByIdOrErr(id: Long): Goods {
        return findById(id) ?: throw RuntimeException("找不到该Goods，id: $id")
    }

}