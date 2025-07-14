package org.morecup.jimmerddd.betterddd.core.bridge

import org.aspectj.lang.ProceedingJoinPoint
import java.lang.reflect.Method

object MethodBridgeConfig {
    var methodBridge: IMethodBridge = DefaultMethodBridge()
}

class DefaultMethodBridge : IMethodBridge {
    override fun beforeMethodInvocation(
        pjp: ProceedingJoinPoint,
        method: Method,
        obj: Any,
        args: Array<Any?>
    ) {
        println("Before method ${method.name} call on $obj with args: ${args.contentToString()}")
    }

//    override fun afterMethodInvocation(
//        pjp: ProceedingJoinPoint,
//        method: Method,
//        obj: Any,
//        args: Array<Any?>,
//        result: Any?
//    ) {
//        println("After method ${method.name} call on $obj, result: $result")
//    }
//
//    override fun aroundMethodInvocation(
//        pjp: ProceedingJoinPoint,
//        method: Method,
//        obj: Any,
//        args: Array<Any?>
//    ): Any? {
//        println("Around method ${method.name} call on $obj")
//        return pjp.proceed()
//    }
}

interface IMethodBridge {
    fun beforeMethodInvocation(pjp: ProceedingJoinPoint, method: Method, obj: Any, args: Array<Any?>)
//    fun afterMethodInvocation(pjp: ProceedingJoinPoint, method: Method, obj: Any, args: Array<Any?>, result: Any?)
//    fun aroundMethodInvocation(pjp: ProceedingJoinPoint, method: Method, obj: Any, args: Array<Any?>): Any?
}
