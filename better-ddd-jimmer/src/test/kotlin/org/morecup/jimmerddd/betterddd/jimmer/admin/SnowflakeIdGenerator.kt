package org.morecup.jimmerddd.betterddd.jimmer.admin

import cn.hutool.core.lang.Snowflake
import cn.hutool.core.util.IdUtil
import org.babyfish.jimmer.sql.meta.UserIdGenerator

class SnowflakeIdGenerator : UserIdGenerator<Long> {
    companion object {
        val snowflake: Snowflake = IdUtil.getSnowflake(1, 1)
    }

    override fun generate(entityType: Class<*>): Long? {
        return snowflake.nextId()
    }
}