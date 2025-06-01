package org.morecup.jimmerddd.core.preanalysis

import org.morecup.jimmerddd.core.domain.order.OrderImpl
import org.morecup.jimmerddd.core.preanalysis.ClassMethodAnalyzer.Companion.analyze
import kotlin.jvm.java
import kotlin.test.Test

class MethodCallAnalyzerTest {
    @Test
    fun test(){
        val analyze = analyze(OrderImpl::class.java.name)
        analyze.printResults()
    }

}