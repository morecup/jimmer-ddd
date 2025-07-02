package org.morecup.jimmerddd.betterddd.jimmer.domain.goods


class HubeiAddress(
    override var name: String,
    override var detail: String,
    var hubeiAddressCode: String
) : Address {
}