package org.morecup.jimmerddd.betterddd.aspect

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.morecup.jimmerddd.betterddd.core.bridge.ConstructorBridgeConfig

@Aspect
class AggregateRootConstructorAspect {

    @Pointcut("execution(*.new(..)) && @within(org.morecup.jimmerddd.betterddd.core.annotation.AggregateRoot)")
    fun constructorOfAggregateRoot() {}

    @Before("constructorOfAggregateRoot()")
    fun beforeConstructor(joinPoint: JoinPoint) {
        ConstructorBridgeConfig.constructorBridge.createInstance(joinPoint as ProceedingJoinPoint, joinPoint.args)
//        println("About to construct AggregateRoot object: ${joinPoint.signature}")

    }

    // If using @Around to intercept, note that constructors don't have return values, so you can't replace the instance
//    @Around("constructorOfAggregateRoot()")
//    fun aroundConstructor(pjp: ProceedingJoinPoint): Any? {
//        println("Around advice: Constructing AggregateRoot object, arguments: ${pjp.args.contentToString()}")
//
//        val obj = pjp.proceed()
//
//        println("Construction completed: $obj") // obj is null here because constructors don't return values
//
//        return obj
//    }

//    @Pointcut("call(*.new(..)) && @within(org.morecup.jimmerddd.betterddd.annotation.AggregateRoot)")
//    fun callConstructorOfAggregateRoot() {}
}