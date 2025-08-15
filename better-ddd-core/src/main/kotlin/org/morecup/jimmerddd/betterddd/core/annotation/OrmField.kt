package org.morecup.jimmerddd.betterddd.core.annotation

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class OrmField(
    val columnName: String = "",
    val columnType: KClass<*> = Any::class,
    val columnChoiceNames: Array<String> = [],
    val columnChoiceTypes: Array<KClass<*>> = [],
    val columnChoiceRule: ColumnChoiceRule = ColumnChoiceRule.auto
)

enum class ColumnChoiceRule {
    auto,
    AND,
    OR
}
