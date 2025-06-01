package org.morecup.jimmerddd.core.domain.order

import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.OneToMany
import org.babyfish.jimmer.sql.OneToOne
import org.morecup.jimmerddd.core.domain.BaseEntity

@Entity
interface User: BaseEntity {

    val name: String
    val email: String?

    // 一对多：用户视角的反向关联
    @OneToMany(mappedBy = "user")
    val orders: List<Order>

    @OneToOne
    val userDetail:UserDetail
}