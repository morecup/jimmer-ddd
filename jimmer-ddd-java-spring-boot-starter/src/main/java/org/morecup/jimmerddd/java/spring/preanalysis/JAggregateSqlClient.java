package org.morecup.jimmerddd.java.spring.preanalysis;

import org.babyfish.jimmer.runtime.DraftSpi;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.AssociatedSaveMode;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.babyfish.jimmer.sql.ast.mutation.SimpleSaveResult;
import org.morecup.jimmerddd.core.aggregateproxy.BaseAssociatedFixedKt;
import org.morecup.jimmerddd.core.preanalysis.MethodInfo;
import org.morecup.jimmerddd.java.preanalysis.FunctionFetcher;

import java.lang.invoke.SerializedLambda;

public class JAggregateSqlClient {
    public JSqlClient sqlClient;

    public JAggregateSqlClient(JSqlClient sqlClient) {
        this.sqlClient = sqlClient;
    }

    public <T> T findById(Class<T> entityClazz, MethodInfo methodInfo, Long id) {
        return sqlClient.findById(FunctionFetcher.of(entityClazz,methodInfo), id);
    }

    public <T> T findById(Class<T> entityClazz, SerializedLambda serializedLambda, Long id) {
        return sqlClient.findById(FunctionFetcher.of(entityClazz,serializedLambda), id);
    }

    public <T> SimpleSaveResult<T> saveAggregate(T entity){
        T impl = entity;
        if (entity instanceof DraftSpi) {
            impl = (T)((DraftSpi) entity).__resolve();
        }
        return sqlClient.saveCommand(BaseAssociatedFixedKt.baseAssociatedFixed(entity)).setMode(SaveMode.NON_IDEMPOTENT_UPSERT).setAssociatedModeAll(AssociatedSaveMode.REPLACE).execute();
    }
}
