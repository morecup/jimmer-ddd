package org.morecup.jimmerddd.core.aggregateproxy

import org.babyfish.jimmer.meta.ImmutableProp
import org.babyfish.jimmer.runtime.DraftContext
import org.babyfish.jimmer.runtime.DraftSpi
import org.babyfish.jimmer.runtime.ImmutableSpi

internal class PropNameDraftManager(
    private val spiList: List<ImmutableSpi>,
    private val draftContext: DraftContext,
) {
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

    data class PropNameDraftInfo(
        val spi: ImmutableSpi,
        val tempDraft: DraftSpi,
        val prop: ImmutableProp,
        val changedDraft: DraftSpi,
    )

    private fun getPropNameDraftInfo(propName: String): PropNameDraftInfo {
        return propNameDraftInfoMap[propName]!!
    }

    fun getPropByName(propName: String):ImmutableProp{
        return getPropNameDraftInfo(propName).prop
    }

    fun contains(propName: String): Boolean {
        return propNameDraftInfoMap.containsKey(propName)
    }

    fun setTempDraftPropValue(propName:String, value:Any?){
        getPropNameDraftInfo(propName).tempDraft.__set(propName,value)
    }

    fun setChangedDraftPropValue(propName:String, value:Any?){
        getPropNameDraftInfo(propName).changedDraft.__set(propName,value)
    }

    fun getTempDraftPropValue(propName:String):Any?{
        return getPropNameDraftInfo(propName).tempDraft.__get(propName)
    }

    fun getChangedDraftPropValue(propName:String):Any?{
        return getPropNameDraftInfo(propName).changedDraft.__get(propName)
    }

    fun getIdPropValue(propName: String):Any{
        val propNameDraftInfo = getPropNameDraftInfo(propName)
        return propNameDraftInfo.spi.__get(propNameDraftInfo.spi.__type().idProp.id)
    }
}