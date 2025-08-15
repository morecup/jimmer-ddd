package org.morecup.jimmerddd.betterddd.jimmer.domain.goods

import org.springframework.stereotype.Service

class ChangeAddressCmd(
    var id: Long,
    var newAddress: String
)

@Service
class ChangeAddressCmdHandler(
    private val goodsRepository: GoodsRepository
){
    fun handle(cmd: ChangeAddressCmd){
        val goods = goodsRepository.findByIdOrErr(cmd.id)
        goods.changeAddress(cmd.newAddress)
        goodsRepository.saveGoods(goods)
    }
}