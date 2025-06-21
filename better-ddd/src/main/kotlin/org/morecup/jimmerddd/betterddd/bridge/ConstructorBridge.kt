package org.morecup.jimmerddd.betterddd.bridge

import org.aspectj.lang.ProceedingJoinPoint

object ConstructorBridgeConfig {
    var constructorBridge: IConstructorBridge = DefaultConstructorBridge()
}

class DefaultConstructorBridge: IConstructorBridge {
    override fun createInstance(pjp: ProceedingJoinPoint, args: Array<Any?>) {
        println("createInstance: ${pjp.signature}, args: ${args.contentToString()}")
    }
}

interface IConstructorBridge {
    fun createInstance(pjp: ProceedingJoinPoint, args: Array<Any?>)
}