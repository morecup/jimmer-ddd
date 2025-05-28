package org.morecup.jimmerddd.core.domain.order

import org.morecup.jimmerddd.core.domain.BaseEntity
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.IdView
import org.babyfish.jimmer.sql.ManyToMany
import org.babyfish.jimmer.sql.ManyToOne
import org.babyfish.jimmer.sql.OneToMany
import org.babyfish.jimmer.sql.OneToOne
import org.babyfish.jimmer.sql.Table
import org.morecup.jimmerddd.core.aggregateproxy.AggregateProxy
import org.morecup.jimmerddd.core.aggregateproxy.GlobalContext.nullDraftContext
import org.morecup.jimmerddd.core.annotation.AggregatedField
import org.morecup.jimmerddd.core.annotation.AggregationType
import org.morecup.jimmerddd.core.annotation.Lazy
import org.morecup.jimmerddd.core.domain.DomainEvent
import org.morecup.jimmerddd.core.domain.DomainRegistry.goodsFactory
import org.morecup.jimmerddd.core.domain.DomainRegistry.goodsRepository
import org.morecup.jimmerddd.core.domain.goods.Goods
import org.morecup.jimmerddd.core.domain.goods.dto.CreateGoodsCmd
import kotlin.jvm.java

const val testOrderId = 1921171871529832448

@Entity
@Table(name = "`order`")
interface Order : BaseEntity {

    @Lazy
    val name: String

    @ManyToOne
    val user: User

    @ManyToMany
    val productList: List<Product>

    @ManyToMany(mappedBy = "orderList")
    val giftList:List<Gift>

    @IdView("giftList")
    val giftListIds:List<Long>

    // 一对一：订单与支付单（mappedBy 指向 Payment.order）
    @OneToOne(mappedBy = "order")
    val payment: Payment?

    @OneToMany(mappedBy = "order")
//    @Lazy
    val aftermarketList: List<Aftermarket>

    @OneToOne
    val orderDetail: OrderDetail

    @OneToOne
    @AggregatedField(type = AggregationType.ID_ONLY)
    val goods: Goods?

    @IdView
//    @Transient
    val goodsId: Long?
}
class OrderUtil {
    companion object {
        fun readRun(): String {
            return "Read data from static method"
        }
    }
}
val orderAggregateProxy = AggregateProxy(OrderDraft::class.java)

class OrderImpl(order: OrderDraft) : OrderDraft by order {

    fun removeAftermarket(reason: String):Boolean {
        val list = aftermarketList()
        list.removeAll { t -> t.reason == reason }
        return true
    }

    fun changeAddress(address: String):Boolean {
        orderDetail().address = address
        return true
    }

    fun renameOrderName(newName: String):Boolean {
        name = newName
//        goodsId?.let { RenameEvent(this,newName, it).publish() }
        return true
    }

    fun changeAllReason(newReason: String):Boolean {
        aftermarketList().forEach { t ->
            t.reason = newReason
        }
        return true
    }

    fun addAftermarket(reason: String):Boolean {
//            val aftermarket: Aftermarket = Aftermarket { this.reason = reason }
//            val list = aftermarketList()
//            list.addBy(aftermarket)
//            println(list)
        val list = aftermarketList()
        list.addBy {
            this.reason = reason
        }
        println(aftermarketList())
//        aftermarketList = aftermarketList + Aftermarket { this.reason = reason }
//        aftermarketList().addBy(Aftermarket { this.reason = reason })
//        val draft: AftermarketDraft = AftermarketDraft.`$`.type.draftFactory.apply(DraftContext(null), null) as AftermarketDraft
//        draft.reason = reason
//        aftermarketList().add(draft)
        return true
    }

    fun doSomethingToName():Boolean {
        println(name)
        return true
    }

    fun sendGoods():Boolean {
        nullDraftContext {
            val goods: Goods = goodsFactory().create(CreateGoodsCmd("test", "某个地址"))
            goodsId = goodsRepository().saveGoods(goods).id
        }
        return true
    }

    fun renameUserDetailMsg(){
        println(user().userDetail().createTime)
        println(giftList.forEach { t ->
            println(t.createTime)
        })
        giftList().filter{it.giftName.isNotEmpty()}.forEach{
            it.giftName = "新的名称"
        }
        user().userDetail().msg = "test"
    }

    fun seeIdView(){
        println(goodsId)
    }

    fun setIdView(){
        goodsId = 1924732369399582720L
    }

    fun seeIdOnly(){
        println(goods)
    }

    fun setIdOnly(){
        goods().id = 1924732201233158144L
    }
}
class RenameEvent(
    source: Any,
    val newName: String,
    val goodsId:Long
) : DomainEvent(source)