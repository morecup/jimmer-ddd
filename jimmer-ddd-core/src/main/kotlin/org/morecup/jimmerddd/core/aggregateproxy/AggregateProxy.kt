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
}