package org.morecup.jimmerddd.java.spring.aggregateproxy;

import org.babyfish.jimmer.spring.cfg.support.SpringConnectionManager;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.kt.KSqlClient;
import org.babyfish.jimmer.sql.runtime.JSqlClientImplementor;
import org.morecup.jimmerddd.core.aggregateproxy.BaseAssociatedFixedKt;
import org.springframework.jdbc.datasource.DataSourceUtils;

import java.sql.Connection;

public class IdGenerator {
    public static Object generatorId(JSqlClientImplementor jSqlClient, Class<?> entityType) {
        return generatorId(jSqlClient, entityType, null);
    }

    public static Object generatorId(JSqlClientImplementor jSqlClient, Class<?> entityType, Connection con) {
        if (con == null) {
            con = DataSourceUtils.getConnection(((SpringConnectionManager) jSqlClient.getConnectionManager()).getDataSource());
        }
        return BaseAssociatedFixedKt.commonGeneratorId(jSqlClient, entityType, con);
    }
}
