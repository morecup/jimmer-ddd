package org.morecup.jimmerddd.betterddd.jimmer.domain.goods

import org.morecup.jimmerddd.betterddd.core.annotation.DomainEntity

@DomainEntity(["address"])
interface Address {
    var name:String
    var detail:String
}