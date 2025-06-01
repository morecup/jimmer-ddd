package org.morecup.jimmerddd.kotlin.spring.aggregateproxy

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.ClassUtil.name
import org.babyfish.jimmer.sql.ast.mutation.AssociatedSaveMode
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.babyfish.jimmer.sql.fetcher.impl.FetcherImpl
import org.babyfish.jimmer.sql.fetcher.impl.FetcherImplementor
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.junit.jupiter.api.Test
import org.morecup.jimmerddd.kotlin.spring.App
import org.morecup.jimmerddd.core.aggregateproxy.allAggregationFields
import org.morecup.jimmerddd.kotlin.spring.domain.goods.Goods
import org.morecup.jimmerddd.kotlin.spring.domain.order.Order
import org.morecup.jimmerddd.kotlin.spring.domain.order.OrderImpl
import org.morecup.jimmerddd.kotlin.spring.domain.order.OrderRepository
import org.morecup.jimmerddd.core.domain.order.by
import org.morecup.jimmerddd.core.domain.order.fetchBy
import org.morecup.jimmerddd.core.domain.order.id
import org.morecup.jimmerddd.kotlin.spring.domain.order.orderAggregateProxy
import org.morecup.jimmerddd.kotlin.spring.domain.order.testOrderId
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

//    @Autowired
//    private lateinit var factory: OrderFactory

    @Test
    fun testInAggregateMethodAddAnotherAggregateData() {
        val order1 = sql.findById(FetcherImpl(Order::class.java).allAggregationFields(), testOrderId)!!
        val result = orderAggregateProxy.execAndSave(order1) { impl ->
            OrderImpl(impl).sendGoods()
        }
        println(result.toString())
    }

    @Test
    fun testInAggregateMethodChangeAnotherAggregateData() {
        val order1 = sql.findById(FetcherImpl(Order::class.java).allAggregationFields(), testOrderId)!!
        println(order1.toString().formatJsonString())
        val result = orderAggregateProxy.execAndSave(order1) { impl ->
            OrderImpl(impl).renameOrderName("新的订单名称")
        }
        println(result.toString())
    }

    @Test
    fun allAggregationFieldsOneToManyTest() {
        val order1 = sql.findById(newFetcher(Order::class).by(FetcherImpl(Order::class.java).allAggregationFields()){

        }, testOrderId)!!
        println(order1.toString().formatJsonString())
        val result = orderAggregateProxy.execAndSave(order1) { impl ->
            OrderImpl(impl).addAftermarket("新的售后原因")
        }
        println(result.toString())
    }

    @Test
    fun testFetcher(){
        val order: Order =
            sql.findById(analysisMethodFetcher(Order::class.java,MethodInfo(OrderImpl::renameUserDetailMsg.javaMethod!!)),
                testOrderId
            )!!
        val result = orderAggregateProxy.execAndSave(order) { impl ->
            OrderImpl(impl).renameUserDetailMsg()
        }
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
        val result = orderAggregateProxy.execAndSave(order) { impl ->
            OrderImpl(impl).renameUserDetailMsg()
        }
    }


    @Test
    fun testSeeIdView(){
        val order: Order =
            sql.findById(analysisMethodFetcher(Order::class.java,MethodInfo(OrderImpl::seeIdView.javaMethod!!)),
                testOrderId
            )!!
        val result = orderAggregateProxy.execAndSave(order) { impl ->
            OrderImpl(impl).seeIdView()
        }
    }

    @Test
    fun testSetIdView(){
        val order: Order =
            sql.findById(analysisMethodFetcher(Order::class.java,MethodInfo(OrderImpl::setIdView.javaMethod!!)),
                testOrderId
            )!!
        val result = orderAggregateProxy.execAndSave(order) { impl ->
            OrderImpl(impl).setIdView()
        }
    }

    @Test
    fun testSeeIdOnly(){
        val order: Order =
            sql.findById(analysisMethodFetcher(Order::class.java,MethodInfo(OrderImpl::seeIdOnly.javaMethod!!)),
                testOrderId
            )!!
        val result = orderAggregateProxy.execAndSave(order) { impl ->
            OrderImpl(impl).seeIdOnly()
        }
    }

    @Test
    fun testSetIdOnly(){
        val order: Order =
            sql.findById(analysisMethodFetcher(Order::class.java,MethodInfo(OrderImpl::setIdOnly.javaMethod!!)),
                testOrderId
            )!!
        val result = orderAggregateProxy.execAndSave(order) { impl ->
            OrderImpl(impl).setIdOnly()
        }
    }

    @Test
    fun testUserIdGenerator(){
        sql.save(Goods {
            name = "商品1"
            nowAddress = "上海"
        }, SaveMode.NON_IDEMPOTENT_UPSERT, AssociatedSaveMode.REPLACE)
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
