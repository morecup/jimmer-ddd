package org.morecup.jimmerddd.kotlin.spring.config

import org.babyfish.jimmer.spring.cfg.JimmerAutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.context.ApplicationContext
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.babyfish.jimmer.sql.ast.mutation.AssociatedSaveMode
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.morecup.jimmerddd.core.JimmerDDDConfig

@AutoConfiguration(after = [JimmerAutoConfiguration::class])
open class KotlinJimmerDDDConfig(
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
            kSqlClient.save(entity, SaveMode.NON_IDEMPOTENT_UPSERT, AssociatedSaveMode.REPLACE).modifiedEntity
        }
        JimmerDDDConfig.setEventPublishFunction(applicationContext::publishEvent)
        // 配置 JimmerDDDConfig 的 saveEntitiesFunction
//        JimmerDDDConfig.setUserIdGenerator(SnowflakeIdGenerator())
    }
}