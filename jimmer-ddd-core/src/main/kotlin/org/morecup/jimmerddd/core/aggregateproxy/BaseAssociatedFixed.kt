package org.morecup.jimmerddd.core.aggregateproxy

import org.babyfish.jimmer.meta.TargetLevel
import org.babyfish.jimmer.runtime.DraftSpi
import org.babyfish.jimmer.runtime.ImmutableSpi
import org.babyfish.jimmer.runtime.Internal
import org.babyfish.jimmer.runtime.NonSharedList
import org.morecup.jimmerddd.core.JimmerDDDConfig.getUserIdGenerator

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