package org.morecup.jimmerddd.core.aggregateproxy.multi

import net.bytebuddy.ByteBuddy
import net.bytebuddy.implementation.InvocationHandlerAdapter
import net.bytebuddy.matcher.ElementMatchers
import org.babyfish.jimmer.runtime.ImmutableSpi
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Type

internal open class MultiEntityProxy(
    private val propNameEntityManager:IPropNameEntityManager,
    private val proxyClass: Class<*>,
) {

    companion object{
        private val log = LoggerFactory.getLogger(MultiEntityProxy::class.java)
    }

    /**
     * 代理对象，用于拦截对属性的访问。
     */
    val proxy: Any by lazy { createProxy() }

    protected open fun createProxy(): Any {
        return byteBuddyNewProxyInstance(
            proxyClass.classLoader,
            listOf(proxyClass, ImmutableSpi::class.java,MultiEntity::class.java),
            ProxyInvocationHandler()
        )
    }

    internal fun byteBuddyNewProxyInstance(loader:ClassLoader, interfaces: List<Type>, h:InvocationHandler): Any {
        return ByteBuddy()
            .subclass(Any::class.java)
            .implement(interfaces)
            .method(ElementMatchers.not(ElementMatchers.isDefaultMethod()))
            .intercept(InvocationHandlerAdapter.of(h))
            .make()
            .load(loader)
            .loaded.getDeclaredConstructor().newInstance()
    }

    protected open fun handleOtherMethod(proxy: Any, method: Method, args: Array<Any>?): Pair<Boolean,Any?> {
        return false to null
    }

    inner class ProxyInvocationHandler : InvocationHandler {
        override fun invoke(proxy: Any, method: Method, args: Array<Any>?): Any? {
            toGetterPropNameOrNull(method)?.let {
                return handleGetter(it)
            }
//            // 添加对 hashCode() 方法的处理
            if (method.name == "hashCode" && method.parameterCount == 0) {
                return System.identityHashCode(proxy)
            }
//            判断是否是MultiEntity的方法
            if (method.declaringClass == MultiEntity::class.java){
                if (method.name == "toEntityList"){
                    return propNameEntityManager.entityList
                }else{
                    return proxyClass
                }
            }
            val (success, result) = handleOtherMethod(proxy, method, args)
            if (success) {
                return result
            }
            return method.invoke(propNameEntityManager.proxyDefaultEntity, *args.orEmpty())
        }

        private fun toGetterPropNameOrNull(method: Method): String? {
            if (method.parameterCount == 0 && (method.returnType != Void.TYPE && method.returnType != Unit::class.java)){
                val methodName = method.name
                val propName = if (methodName.startsWith("get")) {
                    methodName.substring(3)
                        .replaceFirstChar { it.lowercase() }
                } else if (methodName.startsWith("is")) {
                    methodName.substring(2)
                        .replaceFirstChar { it.lowercase() }
                } else {
                    methodName
                }
                if (propNameEntityManager.contains(propName)){
                    return propName
                }
            }
            return null
        }

        private fun handleGetter(propName: String): Any? {
            return getField(propName)
        }
    }

    private fun getField(propName: String): Any? {
        return propNameEntityManager.getEntityPropValue(propName)
    }
}