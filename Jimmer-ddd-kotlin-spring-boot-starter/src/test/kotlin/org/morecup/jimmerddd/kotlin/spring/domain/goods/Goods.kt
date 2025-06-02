package org.morecup.jimmerddd.kotlin.spring.domain.goods

import org.babyfish.jimmer.sql.Entity
import org.morecup.jimmerddd.kotlin.aggregateproxy.KAggregateProxy
import org.morecup.jimmerddd.kotlin.spring.domain.BaseEntity

@Entity
interface Goods : BaseEntity {
    val name: String
    val nowAddress: String
}
val goodsAggregateProxy = KAggregateProxy(GoodsDraft::class)
class GoodsImpl(goods: GoodsDraft) : GoodsDraft by goods {
    fun rename(newName: String) {
        this.name = newName
    }
}