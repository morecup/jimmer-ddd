package org.morecup.jimmerddd.betterddd.jimmer.domain.goods

import org.morecup.jimmerddd.betterddd.core.annotation.AggregateRoot
import org.morecup.jimmerddd.betterddd.core.annotation.OrmField
import org.morecup.jimmerddd.betterddd.core.annotation.OrmObject
import org.morecup.jimmerddd.betterddd.core.annotation.PolyListOrmField
import org.morecup.jimmerddd.betterddd.core.annotation.PolyListOrmFields

@AggregateRoot
@OrmObject(["goods"])
class Goods(
    var name: String,
    @field:OrmField("goods:nowAddress")
    var nowAddress1: String,
    var id: Long?=null,
    @field:PolyListOrmFields(
        columnNames = [
            PolyListOrmField(columnName = "base:"),
            PolyListOrmField(columnChoiceNames = ["base:beijingAddress","base:hubeiAddress"],
                columnChoiceTypes = [BeijingAddress::class,HubeiAddress::class],
            )
        ],
        baseListName = "goods:addressEntity",
        baseColumnChoiceNames = ["base:beijingAddress","base:hubeiAddress"],
        baseColumnChoiceTypes = [BeijingAddress::class,HubeiAddress::class],
    )
    var address: MutableList<Address>,
){

//    fun rename(newName: String) {
//        this.name = newName
//    }

    fun changeAddress(newAddress: String) {
//        this.nowAddress1 = newAddress
//        println(nowAddress1)
        println(address.size)
        val hubeiAddress = HubeiAddress("hubeiAddress", "2342", "234324")
        address.add(hubeiAddress)
        val beijingAddress = BeijingAddress("beijingAddress", "2342", "234324")
        address.add(beijingAddress)
//        println(name)
//        address[0].detail = "haha"
//        if (address[0] is BeijingAddress){
//            (address[0] as BeijingAddress).beijingAddressCode = "change1"
//        }
    }
}

//fun main() {
//    val clazz = Goods::class
//
//    // 使用 Kotlin 反射 能获取到参数名称
//    println("=== 使用 Kotlin 反射 ===")
//    clazz.primaryConstructor?.parameters?.forEach { parameter ->
//        println("参数: ${parameter.name}")
//        parameter.annotations.forEach { annotation ->
//            when (annotation) {
//                is OrmField -> {
//                    println("  发现 OrmField: columnName=${annotation.columnName}")
//                }
//                is OrmFields -> {
//                    println("  发现 OrmFields: 包含 ${annotation.ormField.size} 个 OrmField")
//                }
//            }
//        }
//    }
//
//    // 使用 Java 反射 不能获取到参数名称
//    println("\n=== 使用 Java 反射 ===")
//    clazz.java.declaredConstructors.forEach { constructor ->
//        constructor.parameters.forEach { parameter ->
//            println("参数: ${parameter.name}")
//            parameter.annotations.forEach { annotation ->
//                when (annotation) {
//                    is OrmField -> {
//                        println("  发现 OrmField: columnName=${annotation.columnName}")
//                    }
//                    is OrmFields -> {
//                        println("  发现 OrmFields: 包含 ${annotation.ormField.size} 个 OrmField")
//                    }
//                }
//            }
//        }
//    }
//}