package org.morecup.jimmerddd.core

import org.babyfish.jimmer.sql.ast.mutation.AssociatedSaveMode
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.morecup.jimmerddd.core.domain.SnowflakeIdGenerator
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener

@Configuration
open class DDDConfig(
    // 注入 KSqlClient（假设已由其他模块定义）
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
        JimmerDDDConfig.setUserIdGenerator(SnowflakeIdGenerator())
    }
}