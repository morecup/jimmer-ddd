package org.morecup.jimmerddd.core.aggregateproxy.multi

import org.babyfish.jimmer.meta.ImmutableProp
import org.babyfish.jimmer.runtime.ImmutableSpi

class MultiPropNameEntityManager(
    private val spiList: List<ImmutableSpi>,
): IPropNameEntityManager {
    private val propNameDraftInfoMap = mutableMapOf<String, PropNameEntityInfo>()


    init {
        spiList.forEach { spi ->
            val type = spi.__type()
            type.props.forEach { prop ->
                propNameDraftInfoMap[prop.key] = PropNameEntityInfo(prop.value, spi)
            }
        }
    }

    data class PropNameEntityInfo(
        val prop: ImmutableProp,
        val spi: ImmutableSpi,
    )

    override fun contains(propName: String): Boolean {
        return propNameDraftInfoMap.containsKey(propName)
    }

    override fun getEntityPropValue(propName: String): Any? {
        return propNameDraftInfoMap[propName]!!.spi.__get(propName)
    }

    override val proxyDefaultEntity: Any
        get() = spiList[0]
    override val entityList: List<Any>
        get() = spiList

}