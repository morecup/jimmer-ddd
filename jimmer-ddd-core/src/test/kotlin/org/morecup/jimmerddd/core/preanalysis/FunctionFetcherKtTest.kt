package org.morecup.jimmerddd.core.preanalysis

import org.babyfish.jimmer.sql.fetcher.impl.FetcherImplementor
import org.junit.jupiter.api.Test
import org.morecup.jimmerddd.core.domain.order.Order
import org.morecup.jimmerddd.core.domain.order.OrderImpl

class FunctionFetcherKtTest {
    @Test
    fun analysisFunctionFetcher() {
        val analysisFunctionFetcher: FetcherImplementor<Order> =
            analysisFunctionFetcher(OrderImpl::renameUserDetailMsg, Order::class)
        println(analysisFunctionFetcher)
    }

}