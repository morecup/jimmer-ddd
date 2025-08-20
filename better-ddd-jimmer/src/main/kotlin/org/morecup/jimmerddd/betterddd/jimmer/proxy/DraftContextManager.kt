package org.morecup.jimmerddd.betterddd.jimmer.proxy

import org.babyfish.jimmer.runtime.DraftContext

object DraftContextManager {
    val draftContextThreadLocal = ThreadLocal<DraftContext>()

    fun getOrCreate(): DraftContext {
        if (draftContextThreadLocal.get() == null){
            draftContextThreadLocal.set(DraftContext(null))
        }
        return draftContextThreadLocal.get()!!
    }

    fun set(draftContext: DraftContext?) {
        draftContextThreadLocal.set(draftContext)
    }

    fun remove() {
        draftContextThreadLocal.remove()
    }
}


