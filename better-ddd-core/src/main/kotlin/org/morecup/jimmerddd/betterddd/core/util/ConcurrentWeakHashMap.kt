package org.morecup.jimmerddd.betterddd.core.util

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class ConcurrentWeakHashMap<K, V> {
    private val map = ConcurrentHashMap<WeakKey, V>()
    private val referenceQueue = ReferenceQueue<K>()
    private val cleanupLock = Any()

    private inner class WeakKey(
        key: K,
        queue: ReferenceQueue<K>
    ) : WeakReference<K>(key, queue) {
        private val hashCode: Int = key.hashCode()

        override fun hashCode(): Int = hashCode

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ConcurrentWeakHashMap<*, *>.WeakKey) return false
            val thisKey = get()
            val otherKey = other.get()
            return thisKey != null && thisKey == otherKey
        }
    }

    fun put(key: K, value: V) {
        cleanUp()
        map[WeakKey(key, referenceQueue)] = value
    }

    fun get(key: K): V? {
        cleanUp()
        return map.entries.firstOrNull { it.key.get() == key }?.value
    }

    private fun cleanUp() {
        synchronized(cleanupLock) {
            while (true) {
                val ref = referenceQueue.poll() as? ConcurrentWeakHashMap<*, *>.WeakKey ?: break
                map.remove(ref)
            }
        }
    }

    val size: Int get() {
        cleanUp()
        return map.size
    }
}