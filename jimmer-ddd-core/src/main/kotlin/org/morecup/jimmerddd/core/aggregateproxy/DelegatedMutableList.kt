package org.morecup.jimmerddd.core.aggregateproxy

import org.babyfish.jimmer.runtime.ListDraft
import java.util.WeakHashMap
import java.util.function.Predicate
import kotlin.collections.getOrPut

internal object DelegatedMutableListCache {
    // 使用 WeakHashMap 防止内存泄漏（键为弱引用）
    private val cache = WeakHashMap<CacheKey, DelegatedMutableList<*>>()

    // 定义缓存键（包含 tempDraftValue 和 changedDraftValue 的哈希）
    private data class CacheKey(
        val tempDraftHash: ListDraft<*>,
        val changedDraftHash: ListDraft<*>
    )

    // 获取或创建 DelegatedMutableList 实例
    fun <T> getOrPut(
        tempDraftValue: ListDraft<T>,
        changedDraftValue: ListDraft<Any>
    ): DelegatedMutableList<T> {
        // 生成唯一键
        val key = CacheKey(
            tempDraftValue,
            changedDraftValue
        )

        // 检查缓存
        return cache.getOrPut(key) {
            DelegatedMutableList(tempDraftValue, changedDraftValue)
        } as DelegatedMutableList<T>
    }
}

class DelegatedMutableList<E>(
    val proxyDraftList: ListDraft<E>,
    val changedDraftList: ListDraft<Any>,
) : MutableList<E> by proxyDraftList, RandomAccess {
//    override fun draftContext(): DraftContext? {
//        return proxyDraftList.draftContext()
//    }
//
//    override fun resolve(): List<E?>? {
//        return proxyDraftList.resolve()
//    }

    override fun iterator(): MutableIterator<E> = proxyDraftList.iterator()

    // 选择性覆盖需要增强的方法
    override fun add(element: E): Boolean {
        val proxySuccess = proxyDraftList.add(element)
        val changedSuccess = changedDraftList.add(element)
        return proxySuccess && changedSuccess
    }

    override fun remove(element: E): Boolean {
        val proxyIndex = proxyDraftList.indexOf(element)
        if (proxyIndex != -1) {
            removeAt(proxyIndex) // 调用已同步的 removeAt
            return true
        }
        return false
    }

    override fun addAll(elements: Collection<E>): Boolean {
        val proxySuccess = proxyDraftList.addAll( elements)
        val changedSuccess = changedDraftList.addAll( elements)
        return proxySuccess && changedSuccess
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        val proxySuccess = proxyDraftList.addAll(index, elements)
        val changedSuccess = changedDraftList.addAll(index, elements)
        return proxySuccess && changedSuccess
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        // 按元素移除（需记录索引再反向删除）
        val indicesToRemove = elements
            .map { proxyDraftList.indexOf(it) }
            .filter { it != -1 }
            .sortedDescending() // 反向删除避免索引错位

        indicesToRemove.forEach { index ->
            proxyDraftList.removeAt(index)
            changedDraftList.removeAt(index)
        }
        return indicesToRemove.isNotEmpty()
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        // 保留与 elements 匹配的元素（按索引同步）
        val indicesToRemove = proxyDraftList
            .withIndex()
            .filter { it.value !in elements }
            .map { it.index }
            .sortedDescending() // 反向删除

        indicesToRemove.forEach { index ->
            proxyDraftList.removeAt(index)
            changedDraftList.removeAt(index)
        }
        return indicesToRemove.isNotEmpty()
    }

    override fun clear() {
        changedDraftList.clear()
        return proxyDraftList.clear()
    }

    override fun set(index: Int, element: E): E {
        changedDraftList.set(index,element)
        return proxyDraftList.set(index,element)
    }

    override fun add(index: Int, element: E) {
        changedDraftList.add(index,element)
        return proxyDraftList.add(index,element)
    }

    override fun removeAt(index: Int): E {
        changedDraftList.removeAt(index)
        return proxyDraftList.removeAt(index)
    }

    override fun listIterator(): MutableListIterator<E>  = proxyDraftList.listIterator()

    override fun listIterator(index: Int): MutableListIterator<E>  = proxyDraftList.listIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E>  = proxyDraftList.subList(fromIndex, toIndex)

    fun removeIf(filter: Predicate<in E>): Boolean {
        val indicesToRemove = proxyDraftList.mapIndexedNotNull { index, element ->
            if (filter.test(element)){
                index
            }else{
                null
            }
        }.sortedDescending()

        indicesToRemove.forEach { index ->
            proxyDraftList.removeAt(index)
            changedDraftList.removeAt(index)
        }
        return indicesToRemove.isNotEmpty()
    }

    override val size: Int
        get() = proxyDraftList.size

    override fun isEmpty(): Boolean  = proxyDraftList.isEmpty()

    override fun contains(element: E): Boolean  = proxyDraftList.contains(element)

    override fun containsAll(elements: Collection<E>): Boolean  = proxyDraftList.containsAll(elements)

    override fun get(index: Int): E  = proxyDraftList.get(index)

    override fun indexOf(element: E): Int  = proxyDraftList.indexOf(element)

    override fun lastIndexOf(element: E): Int  = proxyDraftList.lastIndexOf(element)

}