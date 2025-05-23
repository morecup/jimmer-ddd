package org.morecup.jimmerddd.core.domain.goods

import org.babyfish.jimmer.sql.Entity
import org.morecup.jimmerddd.core.aggregateproxy.AggregateProxy
import org.morecup.jimmerddd.core.domain.BaseEntity

@Entity
interface Goods : BaseEntity {
    val name: String
    val nowAddress: String
}
val goodsAggregateProxy = AggregateProxy(GoodsDraft::class.java)
class GoodsImpl(goods: GoodsDraft) : GoodsDraft by goods {
    fun rename(newName: String) {
        this.name = newName
    }
}