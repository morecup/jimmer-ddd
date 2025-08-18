package org.morecup.jimmerddd.betterddd.core.annotation

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class OrmField(
    val columnName: String = "",
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class OrmFields(
    val columnNames: Array<String> = [],
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class ListOrmFields(
    val columnNames: Array<String> = [],
    val baseListName: String = "",
)

//@Retention(AnnotationRetention.RUNTIME)
////@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
//annotation class ListOrmField(
//    val columnName: String = "",
//)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class PolyOrmFields(
    val columnNames: Array<PolyOrmField> = [],
    //采用哪种子类的规则
    val columnChoiceNames: Array<String> = [],
    val columnChoiceTypes: Array<KClass<*>> = [],
    val columnChoiceRule: KClass<out ColumnChoice> = NotNullColumnChoice::class
)

@Retention(AnnotationRetention.RUNTIME)
//@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class PolyOrmField(
    // ormObject不参与多态的情况
    val columnName: String = "",
    // 参与多态的情况
    val columnChoiceTypes: Array<KClass<*>> = [],
    val columnChoiceNames: Array<String> = [],
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class PolyListOrmFields(
    val columnNames: Array<PolyListOrmField> = [],
    val baseListName: String = "",
    val baseColumnChoiceNames: Array<String> = [],
    val baseColumnChoiceTypes: Array<KClass<*>> = [],
    val columnChoiceRule: KClass<out ColumnChoice> = NotNullColumnChoice::class
)

@Retention(AnnotationRetention.RUNTIME)
//@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
annotation class PolyListOrmField(
    val columnName: String = "",

    val columnChoiceTypes: Array<KClass<*>> = [],
    val columnChoiceNames: Array<String> = [],
)

interface ColumnChoice {
    fun choice(choiceOrmObjs:List<Any?>,choiceTypes:List<KClass<*>>): KClass<*>
}

class NotNullColumnChoice:ColumnChoice {
    override fun choice(choiceOrmObjs: List<Any?>, choiceTypes: List<KClass<*>>): KClass<*> {
        val notNullIndex = choiceOrmObjs.indexOfLast { it != null }
        if (notNullIndex < 0) {
            throw RuntimeException("所有值都是null找不到非null的obj,$choiceOrmObjs,$choiceTypes")
        }
        return choiceTypes[notNullIndex]
    }
}
