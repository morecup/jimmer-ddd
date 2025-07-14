package org.morecup.jimmerddd.betterddd.jimmer.admin.goods

import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.OneToOne
import org.babyfish.jimmer.sql.Table
import org.morecup.jimmerddd.betterddd.jimmer.admin.BaseEntity

@Entity
@Table(name = "goods")
interface GoodsEntity : BaseEntity {
    val name: String
    val nowAddress: String

    @OneToOne
    val addressEntity:AddressEntity
}