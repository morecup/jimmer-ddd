package org.morecup.jimmerddd.betterddd.jimmer.admin.goods

import org.babyfish.jimmer.sql.DissociateAction
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.OnDissociate
import org.babyfish.jimmer.sql.OneToOne
import org.babyfish.jimmer.sql.Table
import org.morecup.jimmerddd.betterddd.jimmer.admin.BaseEntity

@Entity
@Table(name = "hubei_address")
interface HubeiAddressEntity:BaseEntity {
    val hubeiAddressCode: String

    @OneToOne
    @OnDissociate(DissociateAction.DELETE)
    val addressEntity:AddressEntity
}