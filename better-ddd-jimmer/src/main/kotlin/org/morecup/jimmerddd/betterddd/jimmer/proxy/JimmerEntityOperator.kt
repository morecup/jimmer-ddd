package org.morecup.jimmerddd.betterddd.jimmer.proxy

import org.babyfish.jimmer.runtime.DraftSpi
import org.morecup.jimmerddd.betterddd.core.proxy.IOrmEntityOperator

class JimmerEntityOperator: IOrmEntityOperator {
    override fun getEntityField(entity: Any, fieldList: List<String>): Any? {
        var draft = entity as DraftSpi
        fieldList.forEach {
            draft = draft.__get(it) as DraftSpi
        }
        return draft
    }

    override fun setEntityField(entity: Any, fieldList: List<String>, value: Any?) {
        var draft = entity as DraftSpi
        fieldList.dropLast(1).forEach {
            draft = draft.__get(it) as DraftSpi
        }
        draft.__set(fieldList.last(),value)
    }
}