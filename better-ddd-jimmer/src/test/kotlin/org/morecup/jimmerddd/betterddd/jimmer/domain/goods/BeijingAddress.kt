package org.morecup.jimmerddd.betterddd.jimmer.domain.goods

import org.morecup.jimmerddd.betterddd.core.annotation.DomainEntity
import org.morecup.jimmerddd.betterddd.core.annotation.OrmField
import org.morecup.jimmerddd.betterddd.core.annotation.OrmObject

@DomainEntity
@OrmObject(["address","localAddress"])
class BeijingAddress(
    name: String,
    detail: String,
    @OrmField("localAddress.beijingAddressCode")
    var beijingAddressCode: String
) : Address(name,detail) {
    fun changeName(newName: String) {
        this.name = newName
    }
}