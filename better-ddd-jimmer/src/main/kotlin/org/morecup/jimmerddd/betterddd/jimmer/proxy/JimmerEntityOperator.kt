package org.morecup.jimmerddd.betterddd.jimmer.proxy

import org.babyfish.jimmer.meta.ImmutableType
import org.babyfish.jimmer.runtime.DraftContext
import org.babyfish.jimmer.runtime.DraftSpi
import org.babyfish.jimmer.runtime.Internal
import org.babyfish.jimmer.runtime.ListDraft
import org.morecup.jimmerddd.betterddd.core.proxy.DomainAggregateRoot
import org.morecup.jimmerddd.betterddd.core.proxy.IOrmEntityConstructor
import org.morecup.jimmerddd.betterddd.core.proxy.IOrmEntityOperator
import kotlin.reflect.KClass

class JimmerEntityOperator: IOrmEntityOperator {
    override fun getEntityField(entity: Any, fieldList: List<String>): Any? {
        var draft = entity as DraftSpi
        fieldList.dropLast(1).forEach {
            draft = draft.__get(it) as DraftSpi
        }
        return draft.__get(fieldList.last())
    }

    override fun setEntityField(entity: Any, fieldList: List<String>, value: Any?) {
        if (value == null){
            return
        }
        var draft = entity as DraftSpi
        fieldList.dropLast(1).forEach {
            draft = draft.__get(it) as DraftSpi
        }
        draft.__set(fieldList.last(),value)
    }

    override fun addElementToEntityList(entity: Any, fieldList: List<String>, element: Any) {
        var draft = entity as DraftSpi
        fieldList.dropLast(1).forEach { fieldName ->
            draft = draft.__get(fieldName) as DraftSpi
        }

        val listDraft = draft.__get(fieldList.last()) as ListDraft<Any>
        listDraft.add(element)
    }

    override fun removeElementFromEntityList(entity: Any, fieldList: List<String>, element: Any) {
        var draft = entity as DraftSpi
        fieldList.dropLast(1).forEach { fieldName ->
            draft = draft.__get(fieldName) as DraftSpi
        }

        val listDraft = draft.__get(fieldList.last()) as ListDraft<*>
        listDraft.remove(element)
    }
}

class JimmerEntityConstructor: IOrmEntityConstructor {
    override fun createInstanceList(ormEntityClassList: List<Class<*>>):List<Any> {
        val jimmerDraftList = ormEntityClassList.map {
            val type = ImmutableType.get(it)
            type.draftFactory.apply(DraftContext(null), null)
        }
        return jimmerDraftList
    }
}
