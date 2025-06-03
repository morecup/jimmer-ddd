package org.morecup.jimmerddd.kotlin.spring.preanalysis

import org.babyfish.jimmer.runtime.DraftSpi
import org.babyfish.jimmer.spring.repo.KotlinRepository
import org.babyfish.jimmer.sql.ast.mutation.AssociatedSaveMode
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.babyfish.jimmer.sql.fetcher.Fetcher
import org.babyfish.jimmer.sql.fetcher.impl.FetcherImpl
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.mutation.KSaveCommandPartialDsl
import org.babyfish.jimmer.sql.kt.ast.mutation.KSimpleSaveResult
import org.morecup.jimmerddd.core.aggregateproxy.allAggregationFields
import org.morecup.jimmerddd.core.aggregateproxy.baseAssociatedFixed
import org.morecup.jimmerddd.kotlin.preanalysis.analysisFunctionFetcher
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

fun <T : Any> KSqlClient.findById(type: KClass<T>, id: Any,function: KFunction<*>?): T?{
    val fetcher: Fetcher<T> = if (function == null) {
        FetcherImpl(type.java).allAggregationFields()
    }else{
        analysisFunctionFetcher(type,function)
    }
    return this.findById(fetcher,id)
}

inline fun <reified E : Any, ID : Any> KotlinRepository<E, ID>.findById(id: ID, function: KFunction<*>?): E?{
    val fetcher: Fetcher<E> = if (function == null) {
        FetcherImpl(E::class.java).allAggregationFields()
    }else{
        analysisFunctionFetcher(E::class,function)
    }
    return findById(id,fetcher)
}

fun <E: Any> KSqlClient.saveAggregate(
    entity: E,
    mode: SaveMode = SaveMode.NON_IDEMPOTENT_UPSERT,
    associatedMode: AssociatedSaveMode = AssociatedSaveMode.REPLACE,
    block: (KSaveCommandPartialDsl.() -> Unit)? = null
): KSimpleSaveResult<E> {
    val impl = if (entity is DraftSpi){
        entity.__resolve() as E
    }else{
        entity
    }
    return save(baseAssociatedFixed(impl), mode, associatedMode, block)
}

fun <E: Any, ID : Any> KotlinRepository<E, ID>.saveAggregate(
    entity: E,
    mode: SaveMode = SaveMode.NON_IDEMPOTENT_UPSERT,
    associatedMode: AssociatedSaveMode = AssociatedSaveMode.REPLACE,
    block: (KSaveCommandPartialDsl.() -> Unit)? = null
): KSimpleSaveResult<E>{
    val impl = if (entity is DraftSpi){
        entity.__resolve() as E
    }else{
        entity
    }
    return save(baseAssociatedFixed(impl), mode, associatedMode, block)
}