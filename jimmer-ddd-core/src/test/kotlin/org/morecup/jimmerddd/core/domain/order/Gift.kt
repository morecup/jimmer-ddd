package org.morecup.jimmerddd.core.domain.order

import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.ManyToMany
import org.morecup.jimmerddd.core.domain.BaseEntity

@Entity
interface Gift: BaseEntity {

    val giftName: String


    @ManyToMany
    val orderList:List<Order>
}