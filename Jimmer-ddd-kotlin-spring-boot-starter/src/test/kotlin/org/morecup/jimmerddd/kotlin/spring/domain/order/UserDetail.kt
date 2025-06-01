package org.morecup.jimmerddd.kotlin.spring.domain.order

import org.babyfish.jimmer.sql.Entity
import org.morecup.jimmerddd.kotlin.spring.domain.BaseEntity

@Entity
interface UserDetail: BaseEntity {
    val msg: String
}