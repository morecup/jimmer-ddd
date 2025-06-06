package org.morecup.jimmerddd.java.spring.config;


import org.babyfish.jimmer.spring.cfg.JimmerAutoConfiguration;
import org.babyfish.jimmer.sql.runtime.JSqlClientImplementor;
import org.morecup.jimmerddd.core.JimmerDDDConfig;
import org.morecup.jimmerddd.java.spring.aggregateproxy.IdGenerator;
import org.morecup.jimmerddd.java.spring.factory.JFactoryAop;
import org.morecup.jimmerddd.java.spring.preanalysis.JAggregateSqlClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@AutoConfiguration(after = JimmerAutoConfiguration.class)
public class JavaJimmerDDDConfiguration {

    private final ApplicationContext applicationContext;

    public JavaJimmerDDDConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public JAggregateSqlClient jAggregateSqlClient() {
        return new JAggregateSqlClient(applicationContext);
    }

    @Bean
    public JFactoryAop factoryAop() {
        return new JFactoryAop();
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onContextRefreshed() {
        JSqlClientImplementor jSqlClient = applicationContext.getBean(JSqlClientImplementor.class);
        // 配置查询函数
        JimmerDDDConfig.setFindByIdFunction(jSqlClient::findById);

        // 配置保存函数
        JimmerDDDConfig.setSaveEntityFunction(entity ->
                jAggregateSqlClient().saveAggregate(entity).getModifiedEntity()
        );

        // 配置事件发布函数
        JimmerDDDConfig.setEventPublishFunction(applicationContext::publishEvent);

        // 配置ID生成函数
        JimmerDDDConfig.setIdGeneratorFunction(type ->
                IdGenerator.generatorId(jSqlClient, type)
        );
    }
}