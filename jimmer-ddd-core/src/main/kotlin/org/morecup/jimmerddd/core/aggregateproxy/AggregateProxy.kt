package org.morecup.jimmerddd.core.aggregateproxy

import org.morecup.jimmerddd.core.FindByIdFunction
import org.morecup.jimmerddd.core.JimmerDDDConfig

class AggregateProxy<P : Any> @JvmOverloads constructor(
    private val implInterfaceClass: Class<P>,
    private val findByIdFunction: FindByIdFunction = JimmerDDDConfig.getFindByIdFunction()
) {

    fun <T, R> exec(base: T, implProcessor: (P) -> R): Pair<T, R> {
        val context = ProxyContext<T, P>(base, implInterfaceClass, findByIdFunction)
        return context.execute(implProcessor)
    }

    // 新增支持多个不同类型 base 的方法
    fun <R> execMulti(vararg bases: Any, implProcessor: (P) -> R): Pair<List<Any>, R> {
        val arrayBases = arrayListOf(bases)
        val context = ProxyContextMulti<P>(arrayBases, implInterfaceClass, findByIdFunction)
        return context.execute(implProcessor)
    }
}