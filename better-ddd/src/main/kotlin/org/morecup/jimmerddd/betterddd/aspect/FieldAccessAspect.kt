package org.morecup.jimmerddd.betterddd.aspect

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.FieldSignature
import java.lang.reflect.Field

@Aspect
class FieldAccessAspect {

    @Pointcut("get(* *) && @within(org.morecup.jimmerddd.betterddd.annotation.AggregateRoot) && !within(org.morecup.jimmerddd.betterddd.aspect.FieldAccessAspect)")
    fun anyFieldGet() {}

    @Pointcut("set(* *) && @within(org.morecup.jimmerddd.betterddd.annotation.AggregateRoot) && !within(org.morecup.jimmerddd.betterddd.aspect.FieldAccessAspect)")
    fun anyFieldSet() {}

    @Around("anyFieldGet()")
    fun aroundFieldGet(pjp: ProceedingJoinPoint): Any {
        val fieldSignature = pjp.signature as FieldSignature
        val field = fieldSignature.field

        val originalValue = pjp.proceed()
        println("Intercepted read field ${fieldSignature.fieldType}, original value: $originalValue")

        return if (originalValue is String) {
            "$originalValue-intercepted"
        } else {
            originalValue
        }
    }

    @Around("anyFieldSet()")
    fun aroundFieldSet(pjp: ProceedingJoinPoint) {
        val args = pjp.args
        val originalValue = args[0]
        println("Intercepted write field ${pjp.signature}, original value: $originalValue")

        val newValue = if (originalValue is String) {
            "Intercepted-$originalValue"
        } else {
            originalValue
        }

        pjp.proceed(arrayOf(newValue))
    }
}