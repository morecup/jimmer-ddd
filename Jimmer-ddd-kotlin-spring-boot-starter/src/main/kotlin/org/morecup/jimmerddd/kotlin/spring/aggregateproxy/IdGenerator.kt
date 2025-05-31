package org.morecup.jimmerddd.kotlin.spring.aggregateproxy

import org.babyfish.jimmer.spring.cfg.support.SpringConnectionManager
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.morecup.jimmerddd.core.aggregateproxy.commonGeneratorId
import org.springframework.jdbc.datasource.DataSourceUtils
import java.sql.Connection

fun generatorId(kSqlClient: KSqlClient,entityType: Class<*>,con: Connection? = null): Any? {
    val connection: Connection = con?:DataSourceUtils.getConnection((kSqlClient.javaClient.connectionManager as SpringConnectionManager).dataSource)
    return commonGeneratorId(kSqlClient.javaClient, entityType, connection)
}