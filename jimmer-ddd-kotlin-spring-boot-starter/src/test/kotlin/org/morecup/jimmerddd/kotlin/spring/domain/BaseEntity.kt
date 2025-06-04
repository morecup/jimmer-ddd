package org.morecup.jimmerddd.kotlin.spring.domain

import com.fasterxml.jackson.annotation.JsonFormat
import org.babyfish.jimmer.sql.GeneratedValue
import org.babyfish.jimmer.sql.Id
import org.babyfish.jimmer.sql.MappedSuperclass
import java.time.LocalDateTime

/*
 * see CommonEntityDraftInterceptor
 */
@MappedSuperclass
interface BaseEntity {

    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator::class)
    val id: Long

    /**
     * 创建时间
     * df(2025-01-20 10:00:00)
     */
    @get:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val createTime: LocalDateTime

    /**
     * 修改时间
     * df(2025-01-20 10:00:00)
     */
    @get:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val updateTime: LocalDateTime

    /**
     * 创建者id
     */
    val createBy: Long?

    /**
     * 修改者id
     */
    val updateBy: Long?
}