package org.morecup.jimmerddd.core.aggregateproxy

import org.babyfish.jimmer.meta.ImmutableProp
import org.babyfish.jimmer.runtime.DraftSpi

interface IPropNameDraftManager {
    val changedDraft: DraftSpi
    val tempDraft: DraftSpi
    val proxyClass:Class<*>
    fun getPropByName(propName: String): ImmutableProp
    fun contains(propName: String): Boolean
    fun setTempDraftPropValue(propName: String, value: Any?)
    fun setChangedDraftPropValue(propName: String, value: Any?)
    fun getTempDraftPropValue(propName: String): Any?
    fun getChangedDraftPropValue(propName: String): Any?
    fun getIdPropValue(propName: String): Any
}