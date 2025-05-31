package org.morecup.jimmerddd.core.aggregateproxy

import org.babyfish.jimmer.meta.ImmutableProp
import org.babyfish.jimmer.runtime.DraftContext
import org.babyfish.jimmer.runtime.DraftSpi
import org.babyfish.jimmer.runtime.ImmutableSpi

internal class MultiSpiPropNameManager(
    private val spiList: List<ImmutableSpi>,
    private val draftContext: DraftContext,
): IPropNameDraftManager {
    private val propNameDraftInfoMap = mutableMapOf<String, PropNameDraftInfo>()

    val changedDraftList = mutableListOf<DraftSpi>()

    init {
        // 初始化每个 Draft 的 propName
        spiList.forEach { spi ->
            val type = spi.__type()
            val tempDraft = draftContext.toDraftObject<Any>(spi).let { it as DraftSpi }
            val changedDraft = type.draftFactory.apply(draftContext, null).let {
                it as DraftSpi
                it.__set(type.idProp.id,tempDraft.__get(type.idProp.id))
                it
            }
            changedDraftList.add(changedDraft)
            type.props.forEach { prop ->
                propNameDraftInfoMap[prop.key] = PropNameDraftInfo(spi, tempDraft, prop.value, changedDraft)
            }
        }
    }

    override val proxyClass: Class<*> by lazy {
        return@lazy spiList[0]::class.java.declaringClass.declaringClass
    }

    override val changedDraft: DraftSpi
        get() = changedDraftList[0]

    data class PropNameDraftInfo(
        val spi: ImmutableSpi,
        val tempDraft: DraftSpi,
        val prop: ImmutableProp,
        val changedDraft: DraftSpi,
    )

    private fun getPropNameDraftInfo(propName: String): PropNameDraftInfo {
        return propNameDraftInfoMap[propName]!!
    }

    override fun getPropByName(propName: String):ImmutableProp{
        return getPropNameDraftInfo(propName).prop
    }

    override fun contains(propName: String): Boolean {
        return propNameDraftInfoMap.containsKey(propName)
    }

    override fun setTempDraftPropValue(propName:String, value:Any?){
        getPropNameDraftInfo(propName).tempDraft.__set(propName,value)
    }

    override fun setChangedDraftPropValue(propName:String, value:Any?){
        getPropNameDraftInfo(propName).changedDraft.__set(propName,value)
    }

    override fun getTempDraftPropValue(propName:String):Any?{
        return getPropNameDraftInfo(propName).tempDraft.__get(propName)
    }

    override fun getChangedDraftPropValue(propName:String):Any?{
        return getPropNameDraftInfo(propName).changedDraft.__get(propName)
    }

    override fun getIdPropValue(propName: String):Any{
        val propNameDraftInfo = getPropNameDraftInfo(propName)
        return propNameDraftInfo.spi.__get(propNameDraftInfo.spi.__type().idProp.id)
    }
}