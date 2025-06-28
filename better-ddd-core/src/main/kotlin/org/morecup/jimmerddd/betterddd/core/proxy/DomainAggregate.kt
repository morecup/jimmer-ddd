package org.morecup.jimmerddd.betterddd.core.proxy

import org.aspectj.lang.ProceedingJoinPoint
import org.morecup.jimmerddd.betterddd.core.annotation.OrmField
import org.morecup.jimmerddd.betterddd.core.annotation.OrmObject
import org.morecup.jimmerddd.betterddd.core.bridge.IFieldBridge
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap
import kotlin.arrayOf
import kotlin.collections.arrayListOf

val aggregateRootCache = ConcurrentHashMap<Any, List<Any>>()

class DomainAggregateRoot {
    companion object {
        fun <T> build(clazz: Class<T>,vararg args: Any): T {
            //实例化T
            val aggregateRoot = clazz.newInstance() as T
            aggregateRootCache.put(aggregateRoot as Any, args.toList())
            return aggregateRoot
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
        return OrmEntityOperatorConfig.operator.getEntityField(entityValue, entityFieldStr)
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
        OrmEntityOperatorConfig.operator.setEntityField(entityValue, entityFieldStr, value)
    }

}