package org.morecup.jimmerddd.betterddd.jimmer.domain.goods

import org.morecup.jimmerddd.betterddd.core.annotation.DomainEntity

@DomainEntity(["address"])
abstract class Address(
    var name:String,
    var detail:String
)