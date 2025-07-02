package org.morecup.jimmerddd.betterddd.core.annotation

import kotlin.reflect.KClass

annotation class OrmFields(
    val columnNames: Array<String> = [],
    val columnClass: Array<KClass<*>> = [],
)
