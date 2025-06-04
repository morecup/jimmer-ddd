package org.morecup.jimmerddd.java.spring.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.MappedSuperclass;

import java.time.LocalDateTime;

@MappedSuperclass
public interface BaseEntity {

    /**
     * 获取实体的唯一标识
     * @return 实体的唯一标识
     */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator.class)
    long getId();

    /**
     * 获取创建时间
     * df(2025-01-20 10:00:00)
     * @return 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime getCreateTime();

    /**
     * 获取修改时间
     * df(2025-01-20 10:00:00)
     * @return 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime getUpdateTime();

    /**
     * 获取创建者id
     * @return 创建者id
     */
    Long getCreateBy();

    /**
     * 获取修改者id
     * @return 修改者id
     */
    Long getUpdateBy();
}