package org.morecup.jimmerddd.betterddd.jimmer

import org.babyfish.jimmer.client.EnableImplicitApi
import org.morecup.jimmerddd.betterddd.core.proxy.OrmEntityConstructorConfig
import org.morecup.jimmerddd.betterddd.core.proxy.OrmEntityOperatorConfig
import org.morecup.jimmerddd.betterddd.core.proxy.aggregateRootToOrmEntityClassCache
import org.morecup.jimmerddd.betterddd.jimmer.admin.goods.AddressEntity
import org.morecup.jimmerddd.betterddd.jimmer.admin.goods.BeijingAddressEntity
import org.morecup.jimmerddd.betterddd.jimmer.admin.goods.GoodsEntity
import org.morecup.jimmerddd.betterddd.jimmer.admin.goods.HubeiAddressEntity
import org.morecup.jimmerddd.betterddd.jimmer.domain.goods.BeijingAddress
import org.morecup.jimmerddd.betterddd.jimmer.domain.goods.Goods
import org.morecup.jimmerddd.betterddd.jimmer.domain.goods.HubeiAddress
import org.morecup.jimmerddd.betterddd.jimmer.proxy.JimmerEntityConstructor
import org.morecup.jimmerddd.betterddd.jimmer.proxy.JimmerEntityOperator
import org.springframework.boot.autoconfigure.SpringBootApplication
import javax.annotation.PostConstruct
import kotlin.jvm.java

@EnableImplicitApi
@SpringBootApplication
open class App {
    @PostConstruct
    fun init() {
        OrmEntityOperatorConfig.operator = JimmerEntityOperator()
        OrmEntityConstructorConfig.constructor = JimmerEntityConstructor()
        aggregateRootToOrmEntityClassCache.put(Goods::class.java, listOf(GoodsEntity::class.java))
        aggregateRootToOrmEntityClassCache.put(BeijingAddress::class.java, listOf(AddressEntity::class.java,BeijingAddressEntity::class.java))
        aggregateRootToOrmEntityClassCache.put(HubeiAddress::class.java, listOf(AddressEntity::class.java,HubeiAddressEntity::class.java))

    }
}