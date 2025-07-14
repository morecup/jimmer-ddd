package org.morecup.jimmerddd.betterddd.jimmer.admin.goods

import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.OneToOne
import org.babyfish.jimmer.sql.Table
import org.morecup.jimmerddd.betterddd.jimmer.admin.BaseEntity

@Entity
@Table(name = "address")
interface AddressEntity:BaseEntity {
    var name:String
    var detail:String

    @OneToOne(mappedBy = "addressEntity")
    val beijingAddress:BeijingAddressEntity?

    @OneToOne(mappedBy = "addressEntity")
    val hubeiAddress:HubeiAddressEntity?
}