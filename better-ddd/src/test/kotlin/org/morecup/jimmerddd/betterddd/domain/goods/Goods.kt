package org.morecup.jimmerddd.betterddd.domain.goods

import org.morecup.jimmerddd.betterddd.annotation.AggregateRoot
import org.morecup.jimmerddd.betterddd.annotation.OrmField
import org.morecup.jimmerddd.betterddd.annotation.OrmObject

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