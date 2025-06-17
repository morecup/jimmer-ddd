package org.morecup.jimmerddd.betterddd.domain.goods

class ChangeAddressCmd(
    var id: Long,
    var newAddress: String
)

class ChangeAddressCmdHandler(
    private val goodsRepository: GoodsRepository
){
    fun handle(cmd: ChangeAddressCmd){
        val goods = goodsRepository.findByIdOrErr(cmd.id)
        goods.changeAddress(cmd.newAddress)
        goodsRepository.saveGoods(goods)
    }
}