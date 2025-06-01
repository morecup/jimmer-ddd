package org.morecup.jimmerddd.kotlin.spring.config

import org.babyfish.jimmer.spring.cfg.JimmerAutoConfiguration
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.morecup.jimmerddd.core.JimmerDDDConfig
import org.morecup.jimmerddd.kotlin.spring.aggregateproxy.generatorId
import org.morecup.jimmerddd.kotlin.spring.preanalysis.saveAggregateChanged
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener

@AutoConfiguration(after = [JimmerAutoConfiguration::class])
open class KotlinJimmerDDDConfiguration(
    private val kSqlClient: KSqlClient,
    private val applicationContext: ApplicationContext
) {
    // 监听 Spring 上下文刷新完成事件
    @EventListener(ContextRefreshedEvent::class)
    fun onContextRefreshed() {
        // 配置 JimmerDDDConfig 的 findByIdFunction
        JimmerDDDConfig.setFindByIdFunction{ fetcher, id ->
            kSqlClient.findById(fetcher, id)
        }
        // 配置 JimmerDDDConfig 的 saveEntityFunction
        JimmerDDDConfig.setSaveEntityFunction{ entity ->
            kSqlClient.saveAggregateChanged(entity).modifiedEntity
        }
        JimmerDDDConfig.setEventPublishFunction(applicationContext::publishEvent)

        JimmerDDDConfig.setIdGeneratorFunction {
            generatorId(kSqlClient,it)
        }
    }
}