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
        [
            OrmField(columnName = "goods:addressEntity", columnType = Address::class),
            OrmField(columnNames = ["goods:addressEntity.beijingAddress","goods:addressEntity.hubeiAddress"],
                columnTypes = [BeijingAddress::class,HubeiAddress::class]),
        ]
    )
    var address:List<Address>,
){

    fun rename(newName: String) {
        this.name = newName
    }

    fun changeAddress(newAddress: String) {
        this.nowAddress1 = newAddress
        address[0].detail = "haha"
        if (address[0] is BeijingAddress){
            (address[0] as BeijingAddress).beijingAddressCode = "change1"
        }
    }
}