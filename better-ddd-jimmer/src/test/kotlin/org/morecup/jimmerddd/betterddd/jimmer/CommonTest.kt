package org.morecup.jimmerddd.betterddd.jimmer

import org.morecup.jimmerddd.betterddd.jimmer.domain.goods.ChangeAddressCmd
import org.morecup.jimmerddd.betterddd.jimmer.domain.goods.ChangeAddressCmdHandler
import org.morecup.jimmerddd.betterddd.jimmer.domain.goods.CreateGoodsCmd
import org.morecup.jimmerddd.betterddd.jimmer.domain.goods.CreateGoodsCmdHandle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test

@SpringBootTest(classes = [App::class])
class CommonTest {
    @Autowired
    private lateinit var createGoodsCmdHandle: CreateGoodsCmdHandle
    @Autowired
    private lateinit var changeAddressCmdHandler: ChangeAddressCmdHandler

    @Test
    fun testChangeAddressCmd(){
        changeAddressCmdHandler.handle(ChangeAddressCmd(1,"新的地址"))
    }

    @Test
    fun testCreateGoodsCmd(){
        createGoodsCmdHandle.handle(CreateGoodsCmd("商品1", "地址1"))
    }
}