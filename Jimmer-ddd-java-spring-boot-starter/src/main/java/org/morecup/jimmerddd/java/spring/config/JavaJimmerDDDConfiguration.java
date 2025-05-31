package org.morecup.jimmerddd.java.spring.config;


import org.babyfish.jimmer.spring.cfg.JimmerAutoConfiguration;
import org.babyfish.jimmer.sql.ast.mutation.AssociatedSaveMode;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.babyfish.jimmer.sql.runtime.JSqlClientImplementor;
import org.morecup.jimmerddd.core.JimmerDDDConfig;
import org.morecup.jimmerddd.java.spring.aggregateproxy.IdGenerator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@AutoConfiguration(after = JimmerAutoConfiguration.class)
public class JavaJimmerDDDConfiguration {

    private final JSqlClientImplementor jSqlClient;
    private final ApplicationContext applicationContext;

    public JavaJimmerDDDConfiguration(JSqlClientImplementor jSqlClient, ApplicationContext applicationContext) {
        this.jSqlClient = jSqlClient;
        this.applicationContext = applicationContext;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onContextRefreshed() {
        // 配置查询函数
        JimmerDDDConfig.setFindByIdFunction(jSqlClient::findById);

        // 配置保存函数
        JimmerDDDConfig.setSaveEntityFunction(entity ->
                jSqlClient.save(entity, SaveMode.NON_IDEMPOTENT_UPSERT, AssociatedSaveMode.REPLACE).getModifiedEntity()
        );

        // 配置事件发布函数
        JimmerDDDConfig.setEventPublishFunction(applicationContext::publishEvent);

        // 配置ID生成函数
        JimmerDDDConfig.setIdGeneratorFunction(type ->
                IdGenerator.generatorId(jSqlClient, type)
        );
    }
}