package org.morecup.jimmerddd.betterddd.jimmer.domain.goods

import org.morecup.jimmerddd.betterddd.core.annotation.AggregateRoot
import org.morecup.jimmerddd.betterddd.core.annotation.OrmField
import org.morecup.jimmerddd.betterddd.core.annotation.OrmFields
import org.morecup.jimmerddd.betterddd.core.annotation.OrmObject

@AggregateRoot
@OrmObject(["goods"])
class Goods(
    private var name: String,
    @OrmField("goods:nowAddress")
    private var nowAddress1: String,
    var id: Long?=null,
    @OrmFields(
        ["goods:addressEntity","goods.addressEntity.beijingAddress","goods.addressEntity.hubeiAddress"],
        [Address::class,BeijingAddress::class,HubeiAddress::class]
    )
    var address:Address,
){

    fun rename(newName: String) {
        this.name = newName
    }

    fun changeAddress(newAddress: String) {
        this.nowAddress1 = newAddress
        address.detail = "haha"
        if (address is BeijingAddress){
            (address as BeijingAddress).beijingAddressCode = "change1"
        }
    }
}