package org.morecup.jimmerddd.kotlin.spring.domain.order

import org.babyfish.jimmer.sql.DissociateAction
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.ManyToOne
import org.babyfish.jimmer.sql.OnDissociate
import org.morecup.jimmerddd.kotlin.spring.domain.BaseEntity

@Entity
interface Aftermarket: BaseEntity {
    val reason: String

    @ManyToOne
    @OnDissociate(DissociateAction.DELETE)
    val order: Order
}