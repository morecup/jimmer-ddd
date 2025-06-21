package org.morecup.jimmerddd.betterddd.proxy

import org.aspectj.lang.ProceedingJoinPoint
import org.morecup.jimmerddd.betterddd.annotation.OrmField
import org.morecup.jimmerddd.betterddd.annotation.OrmObject
import org.morecup.jimmerddd.betterddd.bridge.IFieldBridge
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

class DomainAggregateRootField:IFieldBridge{
    override fun getFieldValue(
        pjp: ProceedingJoinPoint,
        field: Field,
        obj: Any
    ): Any? {
        val objects = aggregateRootCache.get(obj)?:throw IllegalStateException("aggregateRootCache not found")
        val fieldFullName = field.getAnnotation(OrmField::class.java)?.columnName?:field.name
        val objectNames = field.declaringClass.getAnnotation(OrmObject::class.java)?.objectNameList?:arrayOf()
        if (fieldFullName.contains(":")){
            val objectName = fieldFullName.substringBefore(":")
            //查找objectName是否在objectNames中第几个
            val index = objectNames.indexOf(objectName)
            if (index != -1){
                val entityValue = objects[index]
                val entityFieldStr = fieldFullName.substringAfter(":")
            }

        }else{

        }
    }

    override fun setFieldValue(
        pjp: ProceedingJoinPoint,
        field: Field,
        obj: Any,
        value: Any?
    ) {

    }

}