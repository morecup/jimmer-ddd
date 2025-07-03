package org.morecup.jimmerddd.betterddd.core.annotation

import kotlin.reflect.KClass

annotation class OrmField(
    val columnName: String = "",
    val columnType: KClass<*> = Any::class,
    val columnNames: Array<String> = [],
    val columnTypes: Array<KClass<*>> = [],
)
