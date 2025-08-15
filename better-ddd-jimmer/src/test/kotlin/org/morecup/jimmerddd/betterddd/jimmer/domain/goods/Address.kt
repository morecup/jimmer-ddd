package org.morecup.jimmerddd.betterddd.jimmer.domain.goods

import org.morecup.jimmerddd.betterddd.core.annotation.DomainEntity

@DomainEntity(["address"])
abstract class Address(
    open var name:String,
    open var detail:String
)