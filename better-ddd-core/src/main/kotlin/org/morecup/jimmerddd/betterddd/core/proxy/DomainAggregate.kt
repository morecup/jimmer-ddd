package org.morecup.jimmerddd.betterddd.core.proxy

import org.aspectj.lang.ProceedingJoinPoint
import org.morecup.jimmerddd.betterddd.core.annotation.OrmField
import org.morecup.jimmerddd.betterddd.core.annotation.OrmFields
import org.morecup.jimmerddd.betterddd.core.annotation.OrmObject
import org.morecup.jimmerddd.betterddd.core.annotation.PolyListOrmFields
import org.morecup.jimmerddd.betterddd.core.annotation.PolyOrmFields
import org.morecup.jimmerddd.betterddd.core.bridge.IConstructorBridge
import org.morecup.jimmerddd.betterddd.core.bridge.IFieldBridge
import org.morecup.jimmerddd.betterddd.core.util.ConcurrentWeakHashMap
import java.lang.reflect.Field
import kotlin.jvm.java
import kotlin.reflect.KClass

//val aggregateRootCache = ConcurrentHashMap<Any, List<Any>>()
var aggregateRootCache = ConcurrentWeakHashMap<Any, List<Any>>()

class DomainAggregateRoot {
    companion object {
        fun <T> build(clazz: Class<T>,vararg args: Any): T {
            //实例化T
            val aggregateRoot = clazz.newInstance() as T
            aggregateRootCache.put(aggregateRoot as Any, args.toList())
            return aggregateRoot
        }

        fun <T : Any> buildK(clazz: KClass<T>, vararg args: Any): T {
            //实例化T
            val aggregateRoot = clazz.java.newInstance() as T
            aggregateRootCache.put(aggregateRoot as Any, args.toList())
            return aggregateRoot
        }

        fun <T : Any> bind(instance: T, vararg args: Any): T {
            //实例化T
            aggregateRootCache.put(instance as Any, args.toList())
            return instance
        }

        fun findOrmObjs(aggregateRoot: Any): List<Any>{
           return aggregateRootCache.get(aggregateRoot)?:throw IllegalStateException("aggregateRootCache not found")
        }
    }
}
val pattern = """^a(\d+)$""".toRegex()  // 正则匹配 a+数字 模式

class DomainAggregateRootField: IFieldBridge {
    override fun getFieldValue(
        pjp: ProceedingJoinPoint,
        field: Field,
        obj: Any
    ): Any? {
        val objects = aggregateRootCache.get(obj)?:throw IllegalStateException("aggregateRootCache not found")
        val objectNames = field.declaringClass.getAnnotation(OrmObject::class.java)?.objectNameList?:arrayOf()
        val ormField: OrmField? = field.getAnnotation(OrmField::class.java)
        val ormFields: OrmFields? = field.getAnnotation(OrmFields::class.java)
        val polyOrmFields: PolyOrmFields? = field.getAnnotation(PolyOrmFields::class.java)
        val polyListOrmFields: PolyListOrmFields? = field.getAnnotation(PolyListOrmFields::class.java)
        return when {
            ormField != null -> {
                val fieldFullName = ormField.columnName
                val (entityValue, entityFieldList) = resolveEntityAndField(fieldFullName, objectNames, objects)
                OrmEntityOperatorConfig.operator.getEntityField(entityValue, entityFieldList)
            }
            ormFields != null -> {
                val ormObjects = ormFields.columnNames.map {
                    val (entityValue, entityFieldList) = resolveEntityAndField(it, objectNames, objects)
                    val ormObject = OrmEntityOperatorConfig.operator.getEntityField(entityValue, entityFieldList)
                    if (ormObject == null) {
                        throw RuntimeException("映射出来的是null,$field")
                    }
                    ormObject
                }
                DomainAggregateRoot.build(field.type, *ormObjects.toTypedArray())
            }
            polyOrmFields != null -> {
                val choiceOrmObjs = polyOrmFields.columnChoiceNames.map {
                    val (entityValue, entityFieldList) = resolveEntityAndField(it, objectNames, objects)
                    val ormObject = OrmEntityOperatorConfig.operator.getEntityField(entityValue, entityFieldList)
                    ormObject
                }
                val chosenType = polyOrmFields.columnChoiceRule.java.newInstance().choice(choiceOrmObjs,polyOrmFields.columnChoiceTypes.toList())
                val ormObjects = polyOrmFields.columnNames.map {
                    val columnName = it.columnName.ifEmpty { it.columnChoiceNames[it.columnChoiceTypes.indexOf(chosenType)] }
                    val (entityValue, entityFieldList) = resolveEntityAndField(columnName, objectNames, objects)
                    val ormObject = OrmEntityOperatorConfig.operator.getEntityField(entityValue, entityFieldList)
                    if (ormObject == null) {
                        throw RuntimeException("映射出来的是null,$field")
                    }
                    ormObject
                }
                DomainAggregateRoot.build(chosenType.java, *ormObjects.toTypedArray())
            }
            polyListOrmFields != null -> {
                val (entityValue, entityFieldList) = resolveEntityAndField(polyListOrmFields.baseListName, objectNames, objects)
                val baseListOrmEntity = OrmEntityOperatorConfig.operator.getEntityField(entityValue, entityFieldList) as List<*>
                baseListOrmEntity.map { baseOrmEntity ->
                    if (baseOrmEntity == null) {
                        throw RuntimeException("映射出来的是null,$field")
                    }
                    val choiceOrmObjs = polyListOrmFields.baseColumnChoiceNames.map {
                        OrmEntityOperatorConfig.operator.getEntityField(baseOrmEntity, it.split("."))
                    }
                    val chosenType = polyListOrmFields.columnChoiceRule.java.newInstance().choice(choiceOrmObjs,polyListOrmFields.baseColumnChoiceTypes.toList())

                    val ormObjects = polyListOrmFields.columnNames.map {
                        val columnName = it.columnName.ifEmpty { it.columnChoiceNames[it.columnChoiceTypes.indexOf(chosenType)] }
                        if (columnName.contains("base:")) {
                            val ormObject = OrmEntityOperatorConfig.operator.getEntityField(baseOrmEntity, columnName.replace("base:", "").split("."))
                            if (ormObject == null) {
                                throw RuntimeException("映射出来的是null,$field")
                            }
                            ormObject
                        } else {
                            val (entityValue, entityFieldList) = resolveEntityAndField(columnName, objectNames, objects)
                            val ormObject = OrmEntityOperatorConfig.operator.getEntityField(entityValue, entityFieldList)
                            if (ormObject == null) {
                                throw RuntimeException("映射出来的是null,$field")
                            }
                            ormObject
                        }
                    }
                    DomainAggregateRoot.build(chosenType.java, *ormObjects.toTypedArray())
                }
            }
            else -> {
                val (entityValue, entityFieldList) = resolveEntityAndField(field.name, objectNames, objects)
                OrmEntityOperatorConfig.operator.getEntityField(entityValue, entityFieldList)
            }
        }
    }

    override fun setFieldValue(
        pjp: ProceedingJoinPoint,
        field: Field,
        obj: Any,
        value: Any?
    ) {
        val objects = aggregateRootCache.get(obj)?:throw IllegalStateException("aggregateRootCache not found")
        val fieldFullName = field.getAnnotation(OrmField::class.java)?.columnName?:field.name
        val objectNames = field.declaringClass.getAnnotation(OrmObject::class.java)?.objectNameList?:arrayOf()
        val (entityValue, entityFieldList) = resolveEntityAndField(fieldFullName, objectNames, objects)
        OrmEntityOperatorConfig.operator.setEntityField(entityValue, entityFieldList, value)
    }

    private fun resolveEntityAndField(fieldFullName: String, objectNames: Array<String>, objects: List<Any>): EntityAndField {
        val entityValue: Any
        val entityFieldStr: String
        if (fieldFullName.contains(":")) {
            val objectName = fieldFullName.substringBefore(":")
            //查找objectName是否在objectNames中第几个
            val index = objectNames.indexOf(objectName)
            if (index != -1) {
                entityValue = objects[index]
                entityFieldStr = fieldFullName.substringAfter(":")
            } else {
                val matchResult = pattern.find(objectName) ?: throw RuntimeException("entity name not found")
                val aIndex = matchResult.groupValues[1].toIntOrNull() ?: throw RuntimeException("entity name not found")
                entityValue = objects[aIndex]
                entityFieldStr = fieldFullName.substringAfter(":")
            }
        } else {
            entityValue = objects[0]
            entityFieldStr = fieldFullName
        }
        return EntityAndField(entityValue, entityFieldStr.split("."))
    }

    private data class EntityAndField(val entityValue: Any, val entityFieldList: List<String>)
}

class DomainAggregateRootConstructor: IConstructorBridge {
    override fun createInstance(pjp: ProceedingJoinPoint, args: Array<Any?>) {
        println("createInstance: ${pjp.signature}, args: ${args.contentToString()}")
        val aggregateRootClass = pjp.signature.declaringType
        val ormEntityClasses = aggregateRootToOrmEntityClassCache.get(aggregateRootClass)?:throw IllegalStateException("aggregateRootToOrmEntityClassCache not found")
        OrmEntityConstructorConfig.constructor.createInstance(ormEntityClasses,pjp.`this`)
    }
}
