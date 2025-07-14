package org.morecup.jimmerddd.betterddd.aspect

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.*
import org.aspectj.lang.reflect.MethodSignature
import org.morecup.jimmerddd.betterddd.core.bridge.MethodBridgeConfig

@Aspect
class MethodAccessAspect {

//    @Pointcut("execution(* *(..)) && @within(org.morecup.jimmerddd.betterddd.core.annotation.AggregateRoot) && !within(org.morecup.jimmerddd.betterddd.aspect.MethodAccessAspect)")
//    fun anyMethodExecution() {}
//
//    @Before("anyMethodExecution()")
//    fun beforeMethodExecution(pjp: ProceedingJoinPoint) {
//        val methodSignature = pjp.signature as MethodSignature
//        MethodBridgeConfig.methodBridge.beforeMethodInvocation(
//            pjp,
//            methodSignature.method,
//            pjp.target,
//            pjp.args
//        )
//    }

//    @AfterReturning(pointcut = "anyMethodExecution()", returning = "result")
//    fun afterMethodExecution(pjp: ProceedingJoinPoint, result: Any?) {
//        val methodSignature = pjp.signature as MethodSignature
//        MethodBridgeConfig.methodBridge.afterMethodInvocation(
//            pjp,
//            methodSignature.method,
//            pjp.target,
//            pjp.args,
//            result
//        )
//    }
//
//    @Around("anyMethodExecution()")
//    fun aroundMethodExecution(pjp: ProceedingJoinPoint): Any? {
//        val methodSignature = pjp.signature as MethodSignature
//        return MethodBridgeConfig.methodBridge.aroundMethodInvocation(
//            pjp,
//            methodSignature.method,
//            pjp.target,
//            pjp.args
//        )
//    }
}