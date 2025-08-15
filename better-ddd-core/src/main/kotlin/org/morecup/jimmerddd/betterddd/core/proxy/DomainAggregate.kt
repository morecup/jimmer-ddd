package org.morecup.jimmerddd.betterddd.core.proxy

import org.aspectj.lang.ProceedingJoinPoint
import org.morecup.jimmerddd.betterddd.core.annotation.OrmField
import org.morecup.jimmerddd.betterddd.core.annotation.OrmObject
import org.morecup.jimmerddd.betterddd.core.bridge.IConstructorBridge
import org.morecup.jimmerddd.betterddd.core.bridge.IFieldBridge
import org.morecup.jimmerddd.betterddd.core.util.ConcurrentWeakHashMap
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap
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

        fun <T : Any> build(clazz: KClass<T>, vararg args: Any): T {
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
        val fieldFullName = field.getAnnotation(OrmField::class.java)?.columnName?:field.name
        val objectNames = field.declaringClass.getAnnotation(OrmObject::class.java)?.objectNameList?:arrayOf()
        val entityValue: Any
        val entityFieldStr: String
        if (fieldFullName.contains(":")){
            val objectName = fieldFullName.substringBefore(":")
            //查找objectName是否在objectNames中第几个
            val index = objectNames.indexOf(objectName)
            if (index != -1){
                entityValue = objects[index]
                entityFieldStr = fieldFullName.substringAfter(":")
            }else{
                val matchResult = pattern.find(objectName)?:throw RuntimeException("entity name not found")
                val aIndex = matchResult.groupValues[1].toIntOrNull()?:throw RuntimeException("entity name not found")
                entityValue = objects[aIndex]
                entityFieldStr = fieldFullName.substringAfter(":")
            }
        }else{
            entityValue = objects[0]
            entityFieldStr = fieldFullName
        }
        return OrmEntityOperatorConfig.operator.getEntityField(entityValue, entityFieldStr.split("."))
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
        val entityValue: Any
        val entityFieldStr: String
        if (fieldFullName.contains(":")){
            val objectName = fieldFullName.substringBefore(":")
            //查找objectName是否在objectNames中第几个
            val index = objectNames.indexOf(objectName)
            if (index != -1){
                entityValue = objects[index]
                entityFieldStr = fieldFullName.substringAfter(":")
            }else{
                val matchResult = pattern.find(objectName)?:throw RuntimeException("entity name not found")
                val aIndex = matchResult.groupValues[1].toIntOrNull()?:throw RuntimeException("entity name not found")
                entityValue = objects[aIndex]
                entityFieldStr = fieldFullName.substringAfter(":")
            }
        }else{
            entityValue = objects[0]
            entityFieldStr = fieldFullName
        }
        OrmEntityOperatorConfig.operator.setEntityField(entityValue, entityFieldStr.split("."), value)
    }

}

class DomainAggregateRootConstructor: IConstructorBridge {
    override fun createInstance(pjp: ProceedingJoinPoint, args: Array<Any?>) {
        println("createInstance: ${pjp.signature}, args: ${args.contentToString()}")
        val aggregateRootClass = pjp.signature.declaringType
        val ormEntityClasses = aggregateRootToOrmEntityClassCache.get(aggregateRootClass)?:throw IllegalStateException("aggregateRootToOrmEntityClassCache not found")
        OrmEntityConstructorConfig.constructor.createInstance(ormEntityClasses,pjp.`this`)
    }
}
