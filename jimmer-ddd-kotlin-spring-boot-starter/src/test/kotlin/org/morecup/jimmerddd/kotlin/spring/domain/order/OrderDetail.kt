package org.morecup.jimmerddd.kotlin.spring.domain.order

import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.OneToOne
import org.morecup.jimmerddd.kotlin.spring.domain.BaseEntity


@Entity
interface OrderDetail: BaseEntity {

    val address: String

    @OneToOne(mappedBy = "orderDetail")
    val order: Order?
}