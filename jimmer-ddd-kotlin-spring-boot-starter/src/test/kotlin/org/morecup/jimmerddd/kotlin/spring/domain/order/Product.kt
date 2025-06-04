package org.morecup.jimmerddd.kotlin.spring.domain.order

import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.ManyToMany
import org.morecup.jimmerddd.kotlin.spring.domain.BaseEntity
import java.math.BigDecimal

@Entity
interface Product: BaseEntity {
    val name: String
    val originalPrice: BigDecimal

    @ManyToMany(mappedBy = "productList")
    val orderList: List<Order>
}