package org.morecup.jimmerddd.betterddd.aspect

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.FieldSignature
import org.morecup.jimmerddd.betterddd.bridge.FieldBridgeConfig
import java.lang.reflect.Field

@Aspect
class FieldAccessAspect {

    @Pointcut("get(* *) && @within(org.morecup.jimmerddd.betterddd.annotation.AggregateRoot) && !within(org.morecup.jimmerddd.betterddd.aspect.FieldAccessAspect)")
    fun anyFieldGet() {}

    @Pointcut("set(* *) && @within(org.morecup.jimmerddd.betterddd.annotation.AggregateRoot) && !within(org.morecup.jimmerddd.betterddd.aspect.FieldAccessAspect)")
    fun anyFieldSet() {}

    @Around("anyFieldGet()")
    fun aroundFieldGet(pjp: ProceedingJoinPoint): Any? {
        val fieldSignature = pjp.signature as FieldSignature
        val field = fieldSignature.field

        return FieldBridgeConfig.fieldBridge.getFieldValue(pjp, field, pjp.target)
    }

    @Around("anyFieldSet()")
    fun aroundFieldSet(pjp: ProceedingJoinPoint) {
        val args = pjp.args
        val originalValue = args[0]

        val fieldSignature = pjp.signature as FieldSignature
        val field = fieldSignature.field
        FieldBridgeConfig.fieldBridge.setFieldValue(pjp, field, pjp.target, originalValue)
    }
}