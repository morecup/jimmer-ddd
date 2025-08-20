package org.morecup.jimmerddd.betterddd.jimmer.domain.goods

import org.morecup.jimmerddd.betterddd.core.annotation.DomainEntity
import org.morecup.jimmerddd.betterddd.core.annotation.OrmObject

//@DomainEntity
//@OrmObject(["address"])
abstract class Address(
    open var name:String,
    open var detail:String
) {
    // 为noarg插件添加无参构造函数
    constructor() : this("", "")
}