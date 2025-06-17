package com.example;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.FieldSignature;

import java.lang.reflect.Field;

@Aspect
public class FieldAccessAspect {

    @Pointcut("get(* *) && @within(org.morecup.jimmerddd.betterddd.annotation.AggregateRoot) && !within(com.example.FieldAccessAspect)")
    public void anyFieldGet() {}

    @Pointcut("set(* *) && @within(org.morecup.jimmerddd.betterddd.annotation.AggregateRoot) && !within(com.example.FieldAccessAspect)")
    public void anyFieldSet() {}

    @Around("anyFieldGet()")
    public Object aroundFieldGet(ProceedingJoinPoint pjp) throws Throwable {
        FieldSignature fieldSignature = (FieldSignature) pjp.getSignature();
        Field field = fieldSignature.getField();

        Object originalValue = pjp.proceed();
        System.out.println("Intercepted read field " + ((FieldSignature) pjp.getSignature()).getFieldType() + ", original value: " + originalValue);

        if (originalValue instanceof String) {
            return originalValue + "-intercepted";
        }
        return originalValue;
    }

    @Around("anyFieldSet()")
    public void aroundFieldSet(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        Object originalValue = args[0];
        System.out.println("Intercepted write field " + pjp.getSignature() + ", original value: " + originalValue);

        Object newValue = originalValue;
        if (originalValue instanceof String) {
            newValue = "Intercepted-" + originalValue;
        }

        pjp.proceed(new Object[]{newValue});
    }
}