package org.morecup.jimmerddd.core.aggregateproxy

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.babyfish.jimmer.meta.TargetLevel
import org.babyfish.jimmer.runtime.DraftSpi
import org.babyfish.jimmer.runtime.ImmutableSpi
import org.babyfish.jimmer.runtime.Internal
import org.babyfish.jimmer.runtime.NonSharedList
import org.babyfish.jimmer.sql.fetcher.impl.FetcherImpl
import org.babyfish.jimmer.sql.fetcher.impl.FetcherImplementor
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.junit.jupiter.api.Test
import org.morecup.jimmerddd.core.App
import org.morecup.jimmerddd.core.domain.SnowflakeIdGenerator
import org.morecup.jimmerddd.core.domain.order.Order
import org.morecup.jimmerddd.core.domain.order.OrderFactory
import org.morecup.jimmerddd.core.domain.order.OrderImpl
import org.morecup.jimmerddd.core.domain.order.OrderRepository
import org.morecup.jimmerddd.core.domain.order.by
import org.morecup.jimmerddd.core.domain.order.dto.CreateOrderCmd
import org.morecup.jimmerddd.core.domain.order.fetchBy
import org.morecup.jimmerddd.core.domain.order.id
import org.morecup.jimmerddd.core.domain.order.orderAggregateProxy
import org.morecup.jimmerddd.core.domain.order.testOrderId
import org.morecup.jimmerddd.core.preanalysis.MethodInfo
import org.morecup.jimmerddd.core.preanalysis.analysisMethodFetcher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.jvm.java
import kotlin.reflect.jvm.javaMethod

@SpringBootTest(classes = [App::class])
class AggregateProxyTest {
    @Autowired
    private lateinit var repository: OrderRepository

    @Autowired
    private lateinit var sql: KSqlClient

    @Autowired
    private lateinit var factory: OrderFactory

    @Test
    fun addData() {
        factory.create(CreateOrderCmd("新的订单1"))
    }

    @Test
    fun testInAggregateMethodAddAnotherAggregateData() {
        val order1 = sql.findById(FetcherImpl(Order::class.java).allAggregationFields(), testOrderId)!!
        val (changedDraft,result) =orderAggregateProxy.exec(order1) { impl ->
            OrderImpl(impl).sendGoods()
        }
        println(changedDraft)
        repository.saveOrder(changedDraft)
        println(result.toString())
    }

    @Test
    fun testInAggregateMethodChangeAnotherAggregateData() {
        val order1 = sql.findById(FetcherImpl(Order::class.java).allAggregationFields(), testOrderId)!!
        println(order1.toString().formatJsonString())
        val (changedDraft,result) =orderAggregateProxy.exec(order1) { impl ->
            OrderImpl(impl).renameOrderName("新的订单名称")
        }
        println(changedDraft)
        repository.saveOrder(changedDraft)
        println(result.toString())
    }

    @Test
    fun allAggregationFieldsOneToManyTest() {
        val order1 = sql.findById(newFetcher(Order::class).by(FetcherImpl(Order::class.java).allAggregationFields()){

        }, testOrderId)!!
        println(order1.toString().formatJsonString())
        val (changedDraft,result) = orderAggregateProxy.exec(order1) { impl ->
            OrderImpl(impl).addAftermarket("新的售后原因")
        }
        println(changedDraft)
        repository.saveOrder(changedDraft.toAssociatedFixed())
        println(result.toString())
    }

    @Test
    fun testFetcher(){
        val order: Order =
            sql.findById(analysisMethodFetcher(Order::class.java,MethodInfo(OrderImpl::renameUserDetailMsg.javaMethod!!)), testOrderId)!!
        val (changedDraft,result) = orderAggregateProxy.exec(order) { impl ->
            OrderImpl(impl).renameUserDetailMsg()
        }
        println(changedDraft)
        repository.saveOrder(changedDraft.toAssociatedFixed())
    }

    @Test
    fun testFetcher1(){
        val analysisMethodFetcher: FetcherImplementor<Order> =
            analysisMethodFetcher(Order::class.java, MethodInfo(OrderImpl::renameUserDetailMsg.javaMethod!!))
        val order1: Order = sql.createQuery(Order::class){
            where(table.id eq testOrderId)
            select(table.fetch(analysisMethodFetcher))
        }.fetchOne()
        val order2: Order = sql.createQuery(Order::class){
            where(table.id eq testOrderId)
            select(table.fetch(newFetcher(Order::class).`by`(analysisMethodFetcher){

            }))
        }.fetchOne()
        val order: Order = sql.createQuery(Order::class){
                where(table.id eq testOrderId)
                select(table.fetchBy {

                })
            }.fetchOne()
        val (changedDraft,result) = orderAggregateProxy.exec(order) { impl ->
            OrderImpl(impl).renameUserDetailMsg()
        }
        println(changedDraft)
        repository.saveOrder(changedDraft.toAssociatedFixed())
    }

}

fun String.formatJsonString(): String {
    val objectMapper = ObjectMapper().apply {
        // 关键配置：启用缩进
        enable(SerializationFeature.INDENT_OUTPUT)
    }

    return try {
        // 步骤 1: 将字符串解析为 JsonNode
        val jsonNode: JsonNode = objectMapper.readTree(this)

        // 步骤 2: 将 JsonNode 重新序列化为格式化字符串
        objectMapper.writeValueAsString(jsonNode)
    } catch (e: Exception) {
        // 处理无效 JSON 的异常
        throw IllegalArgumentException("Invalid JSON input", e)
    }
}
fun <T> T.toAssociatedFixed(autoAddListId: Boolean = true): T {
    val immutable = this
    val spi = immutable as ImmutableSpi
    val type = spi.__type()
//this::class.java.declaringClass.declaringClass
    return Internal.produce(type, immutable) { draft ->
        type.props.values.forEach { prop ->
            val propId = prop.id
            if (prop.isAssociation(TargetLevel.ENTITY) && spi.__isLoaded(propId)) {
                val target = spi.__get(propId)
                when (target){
                    is MutableList<*> -> {
                        val newList = target.mapNotNull { item ->
                            val itemSpi = item as ImmutableSpi
                            val itemType = itemSpi.__type()
                            val itemIdPropId = itemType.idProp.id

                            if (!itemSpi.__isLoaded(itemIdPropId)&&autoAddListId){
                                val newItem = Internal.produce(prop.targetType, itemSpi){
                                    (it as DraftSpi).__set(itemIdPropId, SnowflakeIdGenerator.snowflake.nextId())
                                }
                                return@mapNotNull newItem.toAssociatedFixed(autoAddListId)
                            }
                            return@mapNotNull item.toAssociatedFixed(autoAddListId)
                        }
                        (draft as DraftSpi).__set(propId, NonSharedList.of(target as NonSharedList<Any>,newList))
                    }
                }
            }
        }
    } as T
}