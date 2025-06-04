package org.morecup.jimmerddd.kotlin.spring.preanalysis

import org.morecup.jimmerddd.core.preanalysis.ClassMethodAnalyzer.Companion.analyze
import org.morecup.jimmerddd.kotlin.spring.domain.order.OrderImpl
import kotlin.test.Test

class MethodCallAnalyzerTest {
    @Test
    fun test(){
        val analyze = analyze(OrderImpl::class.java.name)
        analyze.printResults()
    }

}