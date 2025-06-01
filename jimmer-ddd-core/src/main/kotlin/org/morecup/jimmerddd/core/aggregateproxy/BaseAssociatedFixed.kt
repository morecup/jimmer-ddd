package org.morecup.jimmerddd.core.aggregateproxy

import org.babyfish.jimmer.meta.TargetLevel
import org.babyfish.jimmer.runtime.DraftSpi
import org.babyfish.jimmer.runtime.ImmutableSpi
import org.babyfish.jimmer.runtime.Internal
import org.babyfish.jimmer.runtime.NonSharedList
import org.babyfish.jimmer.sql.meta.UserIdGenerator
import org.babyfish.jimmer.sql.meta.impl.IdentityIdGenerator
import org.babyfish.jimmer.sql.meta.impl.SequenceIdGenerator
import org.babyfish.jimmer.sql.runtime.ExecutionPurpose
import org.babyfish.jimmer.sql.runtime.Executor
import org.babyfish.jimmer.sql.runtime.JSqlClientImplementor
import org.morecup.jimmerddd.core.JimmerDDDConfig.getIdGeneratorFunction
import org.morecup.jimmerddd.core.JimmerDDDException
import java.sql.Connection

@JvmOverloads
fun <T> baseAssociatedFixed(base:T,autoAddListId: Boolean = true): T {
    val immutable = base
    val spi = immutable as ImmutableSpi
    val type = spi.__type()
//this::class.java.declaringClass.declaringClass
    return Internal.produce(type, immutable) { draft ->
        type.props.values.forEach { prop ->
            val propId = prop.id
            if (prop.isAssociation(TargetLevel.ENTITY) && spi.__isLoaded(propId)) {
                val target = spi.__get(propId)
                when (target){
                    is MutableList<*> -> {
                        val newList = target.mapNotNull { item ->
                            val itemSpi = item as ImmutableSpi
                            val itemType = itemSpi.__type()
                            val itemIdPropId = itemType.idProp.id

                            if (!itemSpi.__isLoaded(itemIdPropId)&&autoAddListId){
                                val newItem = Internal.produce(prop.targetType, itemSpi){
                                    (it as DraftSpi).__set(itemIdPropId, getIdGeneratorFunction().invoke(prop.targetType.javaClass))
                                }
                                return@mapNotNull baseAssociatedFixed(newItem,true)
                            }
                            return@mapNotNull baseAssociatedFixed(item,autoAddListId)
                        }
                        (draft as DraftSpi).__set(propId, NonSharedList.of(target as NonSharedList<Any>,newList))
                    }
                }
            }
        }
    } as T
}

fun commonGeneratorId(sqlClient: JSqlClientImplementor,entityType: Class<*>,connection: Connection): Any? {
    val idGenerator = sqlClient.getIdGenerator(entityType)
    if (idGenerator == null) {
        throw JimmerDDDException(
            "Cannot save \"${entityType}\" without id because id generator is not specified"
        )
    }
    return when (idGenerator) {
        is SequenceIdGenerator -> {
            val sql = sqlClient.dialect.getSelectIdFromSequenceSql(idGenerator.sequenceName)
            sqlClient.executor.execute(
                Executor.Args(
                    sqlClient,
                    connection,
                    sql,
                    emptyList<Any?>(),
                    if (sqlClient.sqlFormatter.isPretty) emptyList<Int?>() else null,
                    ExecutionPurpose.MUTATE,
                    sqlClient.exceptionTranslator,
                    null
                ) { stmt, _ ->
                    stmt.executeQuery().use { rs ->
                        rs.next()
                        rs.getObject(1)
                    }
                }
            )
        }
        is UserIdGenerator<*> -> idGenerator.generate(entityType)
        is IdentityIdGenerator -> null
        else -> throw JimmerDDDException(
            "Illegal id generator type: \"${idGenerator.javaClass.name}\", " +
                    "id generator must be sub type of \"${SequenceIdGenerator::class.java.name}\", " +
                    "\"${IdentityIdGenerator::class.java.name}\" or \"${UserIdGenerator::class.java.name}\""
        )
    }
}