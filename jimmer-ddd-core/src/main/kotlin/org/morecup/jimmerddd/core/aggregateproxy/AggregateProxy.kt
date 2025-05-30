package org.morecup.jimmerddd.core.aggregateproxy

import org.babyfish.jimmer.meta.TargetLevel
import org.babyfish.jimmer.runtime.DraftSpi
import org.babyfish.jimmer.runtime.ImmutableSpi
import org.babyfish.jimmer.runtime.Internal
import org.babyfish.jimmer.runtime.NonSharedList
import org.morecup.jimmerddd.core.FindByIdFunction
import org.morecup.jimmerddd.core.JimmerDDDConfig
import org.morecup.jimmerddd.core.JimmerDDDConfig.getUserIdGenerator
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

fun <T> baseAssociatedFixed(base:T,autoAddListId: Boolean = true): T {
    val immutable = base
    val spi = immutable as ImmutableSpi
    val type = spi.__type()
//this::class.java.declaringClass.declaringClass
    return Internal.produce(type, immutable) { draft ->
        type.props.values.forEach { prop ->
            val propId = prop.id
            if (prop.isAssociation(TargetLevel.ENTITY) && spi.__isLoaded(propId)) {
                val target = spi.__get(propId)
                when (target){
                    is MutableList<*> -> {
                        val newList = target.mapNotNull { item ->
                            val itemSpi = item as ImmutableSpi
                            val itemType = itemSpi.__type()
                            val itemIdPropId = itemType.idProp.id

                            if (!itemSpi.__isLoaded(itemIdPropId)&&autoAddListId){
                                val newItem = Internal.produce(prop.targetType, itemSpi){
                                    (it as DraftSpi).__set(itemIdPropId, getUserIdGenerator().generate(prop.targetType.javaClass))
                                }
                                return@mapNotNull baseAssociatedFixed(newItem,true)
                            }
                            return@mapNotNull baseAssociatedFixed(item,autoAddListId)
                        }
                        (draft as DraftSpi).__set(propId, NonSharedList.of(target as NonSharedList<Any>,newList))
                    }
                }
            }
        }
    } as T
}