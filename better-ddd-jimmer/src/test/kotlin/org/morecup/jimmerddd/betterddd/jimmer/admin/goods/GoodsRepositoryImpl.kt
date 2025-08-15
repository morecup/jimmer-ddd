package org.morecup.jimmerddd.betterddd.jimmer.admin.goods

import analysisStackTraceElementCalledMethod
import cn.hutool.http.webservice.SoapUtil
import org.babyfish.jimmer.runtime.DraftContext
import org.babyfish.jimmer.runtime.DraftSpi
import org.babyfish.jimmer.sql.ast.mutation.AssociatedSaveMode
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
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
        kSqlClient.save(changed, SaveMode.NON_IDEMPOTENT_UPSERT, AssociatedSaveMode.REPLACE)
        tempDraft.__draftContext().dispose()
        return
    }

    override fun findByIdOrErr(id: Long): Goods {
        val stackTrace = Thread.currentThread().stackTrace
        if (stackTrace.size > 2) { // 跳过堆栈自身开销和调用方法本身
            // 跳过堆栈自身开销和调用方法本身
            // 处理CGLIB代理情况，需要找到真正的业务调用方法
            var realCallerIndex = 2
            while (realCallerIndex < stackTrace.size) {
                val className = stackTrace[realCallerIndex].className
                // 跳过代理类、CGLIB生成的类以及其他非业务类
                if (!className.contains("$\$EnhancerByCGLIB$$") &&
                    !className.contains("$\$EnhancerBySpringCGLIB$$") &&
                    !className.contains("$\$FastClassByCGLIB$$") &&
                    !className.contains("$\$FastClassBySpringCGLIB$$") &&
                    !className.startsWith("java.") &&
                    !className.startsWith("org.springframework.") &&
                    !className.startsWith("net.sf.cglib.")) {
                    break
                }
                realCallerIndex++
            }

            if (realCallerIndex < stackTrace.size) {
                println("Calling method: ${stackTrace[realCallerIndex].methodName}")
                val stackTraceElement: StackTraceElement = stackTrace[realCallerIndex]
                val analysisStackTraceElementCalledMethod =
                    analysisStackTraceElementCalledMethod(stackTraceElement, Goods::class.java)
                val analysisMethodsCalledFields = analysisMethodsCalledFields(analysisStackTraceElementCalledMethod)
                println(analysisMethodsCalledFields)
                //  FieldInfo -> fetcher
            }
        }
        val goodsEntity: GoodsEntity = kSqlClient.findById(GoodsEntity::class, id) ?: throw RuntimeException("Goods not found")
//        goods-> proxy-> domain goods
        val tempDraft = DraftContext(null).toDraftObject<Any>(goodsEntity).let { it as DraftSpi }
        val goods: Goods = DomainAggregateRoot.build(Goods::class.java, tempDraft)
        return goods
    }


}