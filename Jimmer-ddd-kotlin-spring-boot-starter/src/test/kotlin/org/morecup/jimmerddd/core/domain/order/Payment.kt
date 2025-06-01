package org.morecup.jimmerddd.core.domain.order

import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.OneToOne
import org.morecup.jimmerddd.core.domain.BaseEntity
import java.math.BigDecimal

@Entity
interface Payment:BaseEntity {
    val amount: BigDecimal
    val paymentMethod: String

    @OneToOne
    val order: Order
}