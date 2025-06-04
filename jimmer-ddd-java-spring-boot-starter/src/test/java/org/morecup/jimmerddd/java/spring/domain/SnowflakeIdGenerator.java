package org.morecup.jimmerddd.java.spring.domain;


import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.babyfish.jimmer.sql.meta.UserIdGenerator;

public class SnowflakeIdGenerator implements UserIdGenerator<Long> {

    // 静态成员变量，对应 Kotlin 的 companion object
    private static final Snowflake snowflake = IdUtil.getSnowflake(1, 1);

    @Override
    public Long generate(Class<?> entityType) {
        return snowflake.nextId();
    }
}