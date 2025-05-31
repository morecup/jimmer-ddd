package org.morecup.jimmerddd.kotlin.spring.aggregateproxy

import org.babyfish.jimmer.spring.cfg.support.SpringConnectionManager
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.meta.UserIdGenerator
import org.babyfish.jimmer.sql.meta.impl.IdentityIdGenerator
import org.babyfish.jimmer.sql.meta.impl.SequenceIdGenerator
import org.babyfish.jimmer.sql.runtime.ExecutionPurpose
import org.babyfish.jimmer.sql.runtime.Executor
import org.morecup.jimmerddd.core.JimmerDDDException
import org.springframework.jdbc.datasource.DataSourceUtils
import java.sql.Connection

fun generatorId(kSqlClient: KSqlClient,entityType: Class<*>,con: Connection? = null): Any? {
    val sqlClient = kSqlClient.javaClient
    val idGenerator = sqlClient.getIdGenerator(entityType)
    if (idGenerator == null) {
        throw JimmerDDDException(
            "Cannot save \"${entityType}\" without id because id generator is not specified"
        )
    }
    return when (idGenerator) {
        is SequenceIdGenerator -> {
            val connection: Connection = con?:DataSourceUtils.getConnection((sqlClient.connectionManager as SpringConnectionManager).dataSource)
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