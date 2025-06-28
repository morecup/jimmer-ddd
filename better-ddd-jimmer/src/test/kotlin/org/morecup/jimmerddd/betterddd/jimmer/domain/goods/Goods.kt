package org.morecup.jimmerddd.betterddd.jimmer.domain.goods

import org.morecup.jimmerddd.betterddd.core.annotation.AggregateRoot
import org.morecup.jimmerddd.betterddd.core.annotation.OrmField
import org.morecup.jimmerddd.betterddd.core.annotation.OrmObject

@AggregateRoot
@OrmObject(["goods"])
class Goods(
    var name: String,
    @OrmField("goods.nowAddress")
    var nowAddress1: String,
    var id: Long?=null,
){

    fun rename(newName: String) {
        this.name = newName
    }

    fun changeAddress(newAddress: String) {
        this.nowAddress1 = newAddress
    }
}