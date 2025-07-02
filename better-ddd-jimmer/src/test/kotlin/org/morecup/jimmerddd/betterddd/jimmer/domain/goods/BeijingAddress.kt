package org.morecup.jimmerddd.betterddd.jimmer.domain.goods

import org.morecup.jimmerddd.betterddd.core.annotation.DomainEntity
import org.morecup.jimmerddd.betterddd.core.annotation.OrmField

@DomainEntity(["address","localAddress"])
class BeijingAddress(
    override var name: String,
    override var detail: String,
    @OrmField("localAddress.beijingAddressCode")
    var beijingAddressCode: String
) : Address {
}