package org.morecup.jimmerddd.betterddd.core.bridge

import org.aspectj.lang.ProceedingJoinPoint
import org.morecup.jimmerddd.betterddd.core.proxy.DomainAggregateRootField
import java.lang.reflect.Field

object FieldBridgeConfig {
    var fieldBridge: IFieldBridge = DomainAggregateRootField()
}

class DefaultFieldBridge: IFieldBridge {
    override fun getFieldValue(
        pjp: ProceedingJoinPoint,
        field: Field,
        obj: Any
    ): Any? {
        println("obj: $obj getFieldValue: ${field.name}")
        val originalValue:Any? = pjp.proceed()
        return originalValue
    }

    override fun setFieldValue(
        pjp: ProceedingJoinPoint,
        field: Field,
        obj: Any,
        value: Any?
    ) {
        println("obj: $obj setFieldValue: ${field.name}, value: $value")
        pjp.proceed(pjp.args)
    }

}

interface IFieldBridge {
    fun getFieldValue(pjp: ProceedingJoinPoint,field: Field, obj: Any): Any?
    fun setFieldValue(pjp: ProceedingJoinPoint,field: Field, obj: Any, value: Any?)
}