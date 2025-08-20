package org.morecup.jimmerddd.betterddd.jimmer.domain.goods

import org.morecup.jimmerddd.betterddd.core.annotation.DomainEntity
import org.morecup.jimmerddd.betterddd.core.annotation.OrmField
import org.morecup.jimmerddd.betterddd.core.annotation.OrmObject

@DomainEntity
@OrmObject(["address","localAddress"])
class HubeiAddress(
    override var name: String,
    override var detail: String,
    @field: OrmField("localAddress:hubeiAddressCode")
    var hubeiAddressCode: String
) : Address(name, detail) {
}