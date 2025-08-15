package org.morecup.jimmerddd.betterddd.core.annotation

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class OrmFields(
    val ormField: Array<OrmField> = [],
)
