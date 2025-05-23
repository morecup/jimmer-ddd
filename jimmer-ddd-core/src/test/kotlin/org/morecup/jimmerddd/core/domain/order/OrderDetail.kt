package org.morecup.jimmerddd.core.domain.order

import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.OneToOne
import org.morecup.jimmerddd.core.domain.BaseEntity


@Entity
interface OrderDetail: BaseEntity {

    val address: String

    @OneToOne(mappedBy = "orderDetail")
    val order: Order?
}