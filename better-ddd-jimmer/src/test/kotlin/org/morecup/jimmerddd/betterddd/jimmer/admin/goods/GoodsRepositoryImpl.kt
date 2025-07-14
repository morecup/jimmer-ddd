package org.morecup.jimmerddd.betterddd.jimmer.admin.goods

import analysisStackTraceElementCalledMethod
import org.babyfish.jimmer.runtime.DraftContext
import org.babyfish.jimmer.runtime.DraftSpi
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.morecup.jimmerddd.betterddd.core.preanalysis.analysisMethodsCalledFields
import org.morecup.jimmerddd.betterddd.core.proxy.DomainAggregateRoot
import org.morecup.jimmerddd.betterddd.jimmer.domain.goods.Goods
import org.morecup.jimmerddd.betterddd.jimmer.domain.goods.GoodsRepository
import org.springframework.stereotype.Repository

@Repository
open class GoodsRepositoryImpl(
    private val kSqlClient: KSqlClient
): GoodsRepository {
    override fun saveGoods(goods: Goods) {
        val tempDraft = DomainAggregateRoot.findOrmObjs(goods)[0] as DraftSpi
        val changed = tempDraft.__resolve()
        kSqlClient.save(changed)
        tempDraft.__draftContext().dispose()
        return
    }

    override fun findByIdOrErr(id: Long): Goods {
        val stackTrace = Thread.currentThread().stackTrace
        if (stackTrace.size > 2) { // 跳过堆栈自身开销和调用方法本身
            println("Calling method: ${stackTrace[2].methodName}")
            val stackTraceElement: StackTraceElement = stackTrace[2]
            val analysisStackTraceElementCalledMethod =
                analysisStackTraceElementCalledMethod(stackTraceElement, Goods::class.java)
            val analysisMethodsCalledFields = analysisMethodsCalledFields(analysisStackTraceElementCalledMethod)
            //  FieldInfo -> fetcher
        }
        val goodsEntity: GoodsEntity = kSqlClient.findById(GoodsEntity::class, id) ?: throw RuntimeException("Goods not found")
//        goods-> proxy-> domain goods
        val tempDraft = DraftContext(null).toDraftObject<Any>(goodsEntity).let { it as DraftSpi }
        val goods: Goods = DomainAggregateRoot.build(Goods::class.java, tempDraft)
        return goods
    }


}