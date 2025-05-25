package org.morecup.jimmerddd.kotlin.preanalysis

import org.babyfish.jimmer.spring.repo.KotlinRepository
import org.babyfish.jimmer.sql.fetcher.impl.FetcherImplementor
import org.babyfish.jimmer.sql.kt.KSqlClient
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

fun <T : Any> KSqlClient.findById(type: KClass<T>, id: Any,function: KFunction<*>): T?{
    return this.findById(analysisFunctionFetcher(type,function),id)
}

inline fun <reified E : Any, ID : Any> KotlinRepository<E, ID>.findById(id: ID, function: KFunction<*>): E?{
    val fetcher = analysisFunctionFetcher(E::class,function)
    return findById(id,fetcher)
}

