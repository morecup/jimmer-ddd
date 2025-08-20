package org.morecup.jimmerddd.betterddd.core.proxy

class TrackedAssociationList<T>(
    private val baseListOrmObj: Any,
    private val baseListOrmFieldList: List<String>,
    initialList: List<T>,
    baseList: List<Any>,
    val elementAddListener: (Int,T) -> Any
) : MutableList<T> {

    private val domainEntityList = ArrayList<T>(initialList)

    private val ormBaseList = ArrayList<Any>(baseList)


    override val size: Int
        get() = domainEntityList.size

    override fun isEmpty(): Boolean = domainEntityList.isEmpty()

    override fun contains(element: T): Boolean = domainEntityList.contains(element)

    override fun iterator(): MutableIterator<T> = TrackedIterator(domainEntityList.iterator())

    override fun listIterator(): MutableListIterator<T> = TrackedListIterator(domainEntityList.listIterator())

    override fun listIterator(index: Int): MutableListIterator<T> = TrackedListIterator(domainEntityList.listIterator(index))

    override fun get(index: Int): T = domainEntityList[index]

    override fun indexOf(element: T): Int = domainEntityList.indexOf(element)

    override fun lastIndexOf(element: T): Int = domainEntityList.lastIndexOf(element)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        return TrackedAssociationList(baseListOrmObj, baseListOrmFieldList, domainEntityList.subList(fromIndex, toIndex), ormBaseList.subList(fromIndex, toIndex),elementAddListener)
    }

    // 自定义迭代器类，用于跟踪 remove 操作
    private inner class TrackedIterator(private val iterator: MutableIterator<T>) : MutableIterator<T> by iterator {
        private var lastReturnedIndex = -1

        override fun next(): T {
            val element = iterator.next()
            lastReturnedIndex = domainEntityList.indexOf(element)
            return element
        }

        override fun remove() {
            iterator.remove()
            if (lastReturnedIndex >= 0) {
                // 通知ORM层移除关联关系
                OrmEntityOperatorConfig.operator.removeElementFromEntityList(baseListOrmObj, baseListOrmFieldList, ormBaseList[lastReturnedIndex])
                ormBaseList.removeAt(lastReturnedIndex)
                lastReturnedIndex = -1
            }
        }
    }

    // 自定义列表迭代器类，用于跟踪 remove/set/add 操作
    private inner class TrackedListIterator(private val listIterator: MutableListIterator<T>) : MutableListIterator<T> by listIterator {
        private var lastReturnedIndex = -1

        override fun next(): T {
            val element = listIterator.next()
            lastReturnedIndex = listIterator.previousIndex()
            return element
        }

        override fun previous(): T {
            val element = listIterator.previous()
            lastReturnedIndex = listIterator.nextIndex()
            return element
        }

        override fun remove() {
            listIterator.remove()
            if (lastReturnedIndex >= 0) {
                // 通知ORM层移除关联关系
                OrmEntityOperatorConfig.operator.removeElementFromEntityList(baseListOrmObj, baseListOrmFieldList, ormBaseList[lastReturnedIndex])
                ormBaseList.removeAt(lastReturnedIndex)
                lastReturnedIndex = -1
            }
        }

        override fun set(element: T) {
            listIterator.set(element)
            if (lastReturnedIndex >= 0) {
                // 通知ORM层旧元素被替换
                OrmEntityOperatorConfig.operator.removeElementFromEntityList(baseListOrmObj, baseListOrmFieldList, ormBaseList[lastReturnedIndex])
                ormBaseList[lastReturnedIndex] = elementAddListener(lastReturnedIndex, element)
            }
        }

        override fun add(element: T) {
            val index = listIterator.nextIndex()
            listIterator.add(element)
            if (element != null) {
                val baseOrmObj = elementAddListener(index, element)
                ormBaseList.add(index, baseOrmObj)
            }
            lastReturnedIndex = -1
        }
    }

    // 提取公共逻辑到私有方法
    private fun addElementToBackend(element: T,index:Int = -1): Any? {
        if (element == null) throw RuntimeException("不可添加空元素！")

        val baseOrmObj = elementAddListener(index,element)

        ormBaseList.add(baseOrmObj)

        return baseOrmObj
    }

    override fun add(element: T): Boolean {
        val result = domainEntityList.add(element)
        if (result && element != null) {
            addElementToBackend(element)
        }
        return result
    }

    override fun add(index: Int, element: T) {
        domainEntityList.add(index, element)
        if (element != null) {
            addElementToBackend(element,index)
        }
    }

    override fun addAll(elements: Collection<T>): Boolean {
        var result = false
        for (element in elements) {
            if (add(element)) {
                result = true
            }
        }
        return result
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        var currentIndex = index
        for (element in elements) {
            add(currentIndex, element)
            currentIndex++
        }
        return elements.isNotEmpty()
    }

    override fun remove(element: T): Boolean {
        val index = indexOf(element)
        if (index >= 0) {
            removeAt(index)
            return true
        }
        return false
    }

    override fun removeAt(index: Int): T {
        val element = domainEntityList.removeAt(index)
        if (element != null) {
            // 通知ORM层移除关联关系
            OrmEntityOperatorConfig.operator.removeElementFromEntityList(baseListOrmObj, baseListOrmFieldList, convertToBaseListItem(element))
        }
        return element
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        var modified = false
        for (element in elements) {
            if (remove(element)) {
                modified = true
            }
        }
        return modified
    }

    override fun clear() {
        val elementsToRemove = ArrayList(domainEntityList)
        domainEntityList.clear()
        // 通知ORM层批量移除关联关系
        for (element in elementsToRemove) {
            if (element != null) {
                OrmEntityOperatorConfig.operator.removeElementFromEntityList(baseListOrmObj, baseListOrmFieldList, convertToBaseListItem(element))
            }
        }
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        val elementsToRemove = domainEntityList.filter { it !in elements }
        val result = domainEntityList.retainAll(elements)
        if (result) {
            // 通知ORM层移除不在保留列表中的元素
            for (element in elementsToRemove) {
                if (element != null) {
                    OrmEntityOperatorConfig.operator.removeElementFromEntityList(baseListOrmObj, baseListOrmFieldList, convertToBaseListItem(element))
                }
            }
        }
        return result
    }

    override fun set(index: Int, element: T): T {
        val oldElement = domainEntityList.set(index, element)
        // 通知ORM层旧元素被替换
        if (oldElement != null) {
            OrmEntityOperatorConfig.operator.removeElementFromEntityList(baseListOrmObj, baseListOrmFieldList, convertToBaseListItem(oldElement))
        }
        // 通知ORM层新元素被添加
        if (element != null) {
            addElementToBackend(element,index)
        }
        return oldElement
    }

    override fun containsAll(elements: Collection<T>): Boolean = domainEntityList.containsAll(elements)

    override fun equals(other: Any?): Boolean = domainEntityList == other

    override fun hashCode(): Int = domainEntityList.hashCode()

    override fun toString(): String = domainEntityList.toString()

    private fun convertToBaseListItem(domainObject: T): Any {
        return ormBaseList[domainEntityList.indexOf(domainObject)]
    }
}