package org.morecup.jimmerddd.core.domain.order

import org.babyfish.jimmer.sql.DissociateAction
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.ManyToOne
import org.babyfish.jimmer.sql.OnDissociate
import org.morecup.jimmerddd.core.domain.BaseEntity

@Entity
interface Aftermarket: BaseEntity {
    val reason: String

    @ManyToOne
    @OnDissociate(DissociateAction.DELETE)
    val order: Order
}