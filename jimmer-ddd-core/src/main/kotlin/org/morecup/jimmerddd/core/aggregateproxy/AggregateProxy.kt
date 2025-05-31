package org.morecup.jimmerddd.core.aggregateproxy

import org.morecup.jimmerddd.core.FindByIdFunction
import org.morecup.jimmerddd.core.JimmerDDDConfig
import org.morecup.jimmerddd.core.SaveEntityFunction

class AggregateProxy<P : Any> @JvmOverloads constructor(
    private val implInterfaceClass: Class<P>,
    private val findByIdFunction: FindByIdFunction = JimmerDDDConfig.getFindByIdFunction(),
    private val saveEntityFunction: SaveEntityFunction = JimmerDDDConfig.getSaveEntityFunction()
) {

    fun <T, R> exec(base: T, implProcessor: (P) -> R): Pair<T, R> {
        val context = ProxyContext<T, P>(base, implInterfaceClass, findByIdFunction)
        return context.execute(implProcessor)
    }
    
    fun <T, R> execAndSave(base: T, implProcessor: (P) -> R): R {
        val context = ProxyContext<T, P>(base, implInterfaceClass, findByIdFunction)
        val (changed, result) = context.execute(implProcessor)
        saveEntityFunction.invoke(baseAssociatedFixed(changed!!))
        return result
    }

    /**
     * 执行并保存并且返回修改的实体
     */
    fun <T, R> execAndSaveRM(base: T, implProcessor: (P) -> R): Pair<T, R> {
        val context = ProxyContext<T, P>(base, implInterfaceClass, findByIdFunction)
        val (changed, result) = context.execute(implProcessor)
        val modified = saveEntityFunction.invoke(baseAssociatedFixed(changed!!))
        return modified as T to result
    }

    // 新增支持多个不同类型 base 的方法
    fun <R> execMulti(vararg bases: Any, implProcessor: (P) -> R): Pair<List<Any>, R> {
        val arrayBases = arrayListOf(bases)
        val context = ProxyContextMulti<P>(arrayBases, implInterfaceClass, findByIdFunction)
        return context.execute(implProcessor)
    }

    fun <R> execMultiAndSave(vararg bases: Any, implProcessor: (P) -> R): R {
        val arrayBases = arrayListOf(bases)
        val context = ProxyContextMulti<P>(arrayBases, implInterfaceClass, findByIdFunction)
        val (changed, result) = context.execute(implProcessor)
        changed.forEach { saveEntityFunction.invoke(baseAssociatedFixed(it)) }
        return result
    }
}

