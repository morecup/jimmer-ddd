package org.morecup.jimmerddd.core.preanalysis

import org.babyfish.jimmer.sql.fetcher.impl.FetcherImplementor
import org.junit.jupiter.api.Test
import org.morecup.jimmerddd.core.domain.order.Order
import org.morecup.jimmerddd.core.domain.order.OrderImpl
import kotlin.reflect.jvm.javaMethod

class FunctionFetcherKtTest {
    @Test
    fun analysisFunctionFetcher() {
        val analysisMethodFetcher: FetcherImplementor<Order> =
            analysisMethodFetcher(Order::class.java,MethodInfo(OrderImpl::renameUserDetailMsg.javaMethod!!))
        println(analysisMethodFetcher)
    }

}