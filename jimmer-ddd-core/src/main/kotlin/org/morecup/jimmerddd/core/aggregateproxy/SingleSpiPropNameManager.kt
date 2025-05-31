package org.morecup.jimmerddd.core.aggregateproxy

import org.babyfish.jimmer.meta.ImmutableProp
import org.babyfish.jimmer.runtime.DraftContext
import org.babyfish.jimmer.runtime.DraftSpi
import org.babyfish.jimmer.runtime.ImmutableSpi

internal class SingleSpiPropNameManager(
    private val spi: ImmutableSpi,
    private val draftContext: DraftContext,
): IPropNameDraftManager {
    private val type = spi.__type()

    private val tempDraft = draftContext.toDraftObject<Any>(spi).let { it as DraftSpi }

    override val changedDraft = type.draftFactory.apply(draftContext, null).let {
        it as DraftSpi
        it.__set(type.idProp.id,tempDraft.__get(type.idProp.id))
        it
    }
    private val props = type.props

    override val proxyClass: Class<*> by lazy {
        return@lazy spi::class.java.declaringClass.declaringClass
    }

    override fun getPropByName(propName: String):ImmutableProp{
        return getPropByName(propName)
    }

    override fun contains(propName: String): Boolean {
        return props.containsKey(propName)
    }

    override fun setTempDraftPropValue(propName:String, value:Any?){
        tempDraft.__set(propName,value)
    }

    override fun setChangedDraftPropValue(propName:String, value:Any?){
        changedDraft.__set(propName,value)
    }

    override fun getTempDraftPropValue(propName:String):Any?{
        return tempDraft.__get(propName)
    }

    override fun getChangedDraftPropValue(propName:String):Any?{
        return changedDraft.__get(propName)
    }

    override fun getIdPropValue(propName: String):Any{
        return spi.__get(type.idProp.id)
    }
}