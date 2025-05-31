package org.morecup.jimmerddd.kotlin.spring.preanalysis

import org.babyfish.jimmer.spring.repo.KotlinRepository
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.morecup.jimmerddd.kotlin.preanalysis.analysisFunctionFetcher
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

fun <T : Any> KSqlClient.findById(type: KClass<T>, id: Any,function: KFunction<*>): T?{
    return this.findById(analysisFunctionFetcher(type,function),id)
}

inline fun <reified E : Any, ID : Any> KotlinRepository<E, ID>.findById(id: ID, function: KFunction<*>): E?{
    val fetcher = analysisFunctionFetcher(E::class,function)
    return findById(id,fetcher)
}

