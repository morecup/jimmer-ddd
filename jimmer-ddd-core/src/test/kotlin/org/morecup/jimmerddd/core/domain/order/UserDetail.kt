package org.morecup.jimmerddd.core.domain.order

import org.babyfish.jimmer.sql.Entity
import org.morecup.jimmerddd.core.domain.BaseEntity

@Entity
interface UserDetail: BaseEntity {
    val msg: String
}