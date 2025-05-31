package org.morecup.jimmerddd.kotlin.preanalysis

import org.babyfish.jimmer.sql.fetcher.impl.FetcherImplementor
import org.morecup.jimmerddd.core.preanalysis.MethodInfo
import org.morecup.jimmerddd.core.preanalysis.analysisMethodFetcher
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod

fun <T : Any> analysisFunctionFetcher(entityClazz: KClass<T>,function: KFunction<*>): FetcherImplementor<T> {
    val analysisMethodFetcher: FetcherImplementor<T> =
        analysisMethodFetcher(entityClazz.java, MethodInfo(function.javaMethod!!))
    return analysisMethodFetcher
}