package org.morecup.jimmerddd.betterddd.core.proxy

import org.aspectj.lang.ProceedingJoinPoint
import org.morecup.jimmerddd.betterddd.core.annotation.ListOrmFields
import org.morecup.jimmerddd.betterddd.core.annotation.OrmField
import org.morecup.jimmerddd.betterddd.core.annotation.OrmFields
import org.morecup.jimmerddd.betterddd.core.annotation.OrmObject
import org.morecup.jimmerddd.betterddd.core.annotation.PolyListOrmFields
import org.morecup.jimmerddd.betterddd.core.annotation.PolyOrmFields
import org.morecup.jimmerddd.betterddd.core.bridge.IConstructorBridge
import org.morecup.jimmerddd.betterddd.core.bridge.IFieldBridge
import org.morecup.jimmerddd.betterddd.core.util.ConcurrentWeakHashMap
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import kotlin.jvm.java
import kotlin.reflect.KClass

//val aggregateRootCache = ConcurrentHashMap<Any, List<Any>>()
var aggregateRootCache = ConcurrentWeakHashMap<Any, List<Any>>()

class DomainAggregateRoot {
    companion object {
        fun <T> build(clazz: Class<T>,vararg args: Any): T {
            //实例化T
            val aggregateRoot = clazz.newInstance() as T
            aggregateRootCache.put(aggregateRoot as Any, args.toList())
            return aggregateRoot
        }

        fun <T : Any> buildK(clazz: KClass<T>, vararg args: Any): T {
            //实例化T
            val aggregateRoot = clazz.java.newInstance() as T
            aggregateRootCache.put(aggregateRoot as Any, args.toList())
            return aggregateRoot
        }

        fun <T : Any> bind(instance: T, vararg args: Any): T {
            //实例化T
            aggregateRootCache.put(instance as Any, args.toList())
            return instance
        }

        fun findOrmObjs(aggregateRoot: Any): List<Any>{
           return aggregateRootCache.get(aggregateRoot)?:throw IllegalStateException("aggregateRootCache not found")
        }
    }
}
val pattern = """^a(\d+)$""".toRegex()  // 正则匹配 a+数字 模式

// 添加字段值缓存
val fieldValueCache = ConcurrentWeakHashMap<Pair<Any, Field>, Any?>()

class DomainAggregateRootField: IFieldBridge {
    override fun getFieldValue(
        pjp: ProceedingJoinPoint,
        field: Field,
        obj: Any
    ): Any? {
        // 增加缓存
        // 先从缓存中获取
        val cacheKey = Pair(obj, field)
        if (fieldValueCache.get(cacheKey) != null) {
            return fieldValueCache.get(cacheKey)
        }
        // 处理非基础类型情况
        val objects = aggregateRootCache.get(obj)?:throw IllegalStateException("aggregateRootCache not found")
        val objectNames = field.declaringClass.getAnnotation(OrmObject::class.java)?.objectNameList?:arrayOf()
        val ormField: OrmField? = field.getAnnotation(OrmField::class.java)
        val ormFields: OrmFields? = field.getAnnotation(OrmFields::class.java)
        val listOrmFields: ListOrmFields? = field.getAnnotation(ListOrmFields::class.java)
        val polyOrmFields: PolyOrmFields? = field.getAnnotation(PolyOrmFields::class.java)
        val polyListOrmFields: PolyListOrmFields? = field.getAnnotation(PolyListOrmFields::class.java)
        val result = when {
            ormField != null -> {
                val fieldFullName = ormField.columnName
                val (entityValue, entityFieldList) = resolveEntityAndField(fieldFullName, objectNames, objects)
                val fieldValue = OrmEntityOperatorConfig.operator.getEntityField(entityValue, entityFieldList)
                // field.type判断是否是基础orm类型，而不是自定义类型
                if (isBasicOrmType(field.type)) {
                    fieldValue
                } else {
                    if (fieldValue == null) {
                        null
                    } else {
                        DomainAggregateRoot.build(field.type, fieldValue)
                    }
                }
            }
            ormFields != null -> {
                val ormObjects = ormFields.columnNames.map {
                    val (entityValue, entityFieldList) = resolveEntityAndField(it, objectNames, objects)
                    val ormObject = OrmEntityOperatorConfig.operator.getEntityField(entityValue, entityFieldList)
                    if (ormObject == null) {
                        throw RuntimeException("映射出来的是null,$field")
                    }
                    ormObject
                }
                DomainAggregateRoot.build(field.type, *ormObjects.toTypedArray())
            }
            listOrmFields!= null -> {
                val (baseListOrmObj, baseListOrmFieldList) = resolveEntityAndField(listOrmFields.baseListName, objectNames, objects)
                val baseListOrmEntity = OrmEntityOperatorConfig.operator.getEntityField(baseListOrmObj, baseListOrmFieldList) as List<Any>
                val mappedList = baseListOrmEntity.map { baseOrmEntity ->
                    if (baseOrmEntity == null) {
                        throw RuntimeException("映射出来的是null,$field")
                    }
                    val ormObjects = listOrmFields.columnNames.map {
                        if (it.contains("base:")) {
                            val ormObject = OrmEntityOperatorConfig.operator.getEntityField(baseOrmEntity, it.replace("base:", "").split("."))
                            if (ormObject == null) {
                                throw RuntimeException("映射出来的是null,$field")
                            }
                            ormObject
                        } else {
                            val (entityValue, entityFieldList) = resolveEntityAndField(it, objectNames, objects)
                            val ormObject = OrmEntityOperatorConfig.operator.getEntityField(entityValue, entityFieldList)
                            if (ormObject == null) {
                                throw RuntimeException("映射出来的是null,$field")
                            }
                            ormObject
                        }
                    }
                    DomainAggregateRoot.build(field.type, *ormObjects.toTypedArray())
                }

                val withIndexCustomNames = listOrmFields.columnNames.withIndex()
                    .filter { it.value.contains("base:") }
                    .sortedBy { it.value.length }
                    .map { IndexedValue(it.index, it.value.substringAfter("base:")) }
                // 使用可追踪的List实现，以便在添加/删除元素时通知ORM层
                TrackedAssociationList(baseListOrmObj, baseListOrmFieldList, mappedList,baseListOrmEntity)
                { index,addedDomainEntity ->
                    val ormObjs = DomainAggregateRoot.findOrmObjs(addedDomainEntity)
                    // 先查找withIndexCustomNames中是否存在空字符串，如果存在，则获取对应ormobj 添加到ormBaseList中，如果不存在，则创建一个rmobj，添加到ormBaseList中
                    val emptyNameIndex = withIndexCustomNames.indexOfFirst { it.value.isEmpty() }
                    val baseOrmObj = if (emptyNameIndex != -1) {
                        ormObjs[emptyNameIndex]
                    } else {
                        // 创建一个新的ORM对象
                        // 这里假设使用第一个ORM对象的类型来创建新实例
                        // 实际实现可能需要根据具体业务需求调整
                        val baseListOrm = OrmEntityOperatorConfig.operator.getEntityField(baseListOrmObj, baseListOrmFieldList)  as List<Any>
                        // 获取list内实际的泛型
                        val baseListOrmType = baseListOrm.javaClass.genericSuperclass as ParameterizedType
                        val baseListOrmGenericType = baseListOrmType.actualTypeArguments[0]
                        // 创建一个新的ORM对象
                        OrmEntityConstructorConfig.constructor.createInstance(baseListOrmGenericType as Class<*>)
                    }
                    if (index >=0){
                        OrmEntityOperatorConfig.operator.addElementToEntityListAt(baseListOrmObj, baseListOrmFieldList, index,baseOrmObj)
                    }else{
                        OrmEntityOperatorConfig.operator.addElementToEntityList(baseListOrmObj, baseListOrmFieldList, baseOrmObj)
                    }

                    withIndexCustomNames.forEach {
                        if (it.value.isNotBlank()){
                            OrmEntityOperatorConfig.operator.setEntityField(baseOrmObj, it.value.split("."), ormObjs[it.index])
                        }
                    }

                    baseOrmObj
                }
            }
            polyOrmFields != null -> {
                val choiceOrmObjs = polyOrmFields.columnChoiceNames.map {
                    val (entityValue, entityFieldList) = resolveEntityAndField(it, objectNames, objects)
                    val ormObject = OrmEntityOperatorConfig.operator.getEntityField(entityValue, entityFieldList)
                    ormObject
                }
                val chosenType = polyOrmFields.columnChoiceRule.java.newInstance().choice(choiceOrmObjs,polyOrmFields.columnChoiceTypes.toList())
                val ormObjects = polyOrmFields.columnNames.map {
                    val columnName = it.columnName.ifEmpty { it.columnChoiceNames[it.columnChoiceTypes.indexOf(chosenType)] }
                    val (entityValue, entityFieldList) = resolveEntityAndField(columnName, objectNames, objects)
                    val ormObject = OrmEntityOperatorConfig.operator.getEntityField(entityValue, entityFieldList)
                    if (ormObject == null) {
                        throw RuntimeException("映射出来的是null,$field")
                    }
                    ormObject
                }
                DomainAggregateRoot.build(chosenType.java, *ormObjects.toTypedArray())
            }
            polyListOrmFields != null -> {
                val (baseListOrmObj, baseListOrmFieldList) = resolveEntityAndField(polyListOrmFields.baseListName, objectNames, objects)
                val baseListOrmEntity = OrmEntityOperatorConfig.operator.getEntityField(baseListOrmObj, baseListOrmFieldList) as List<Any>
                val mappedList = baseListOrmEntity.map { baseOrmEntity ->
                    if (baseOrmEntity == null) {
                        throw RuntimeException("映射出来的是null,$field")
                    }
                    val choiceOrmObjs = polyListOrmFields.baseColumnChoiceNames.map {
                        OrmEntityOperatorConfig.operator.getEntityField(baseOrmEntity, it.replace("base:", "").split("."))
                    }
                    val chosenType = polyListOrmFields.columnChoiceRule.java.newInstance().choice(choiceOrmObjs,polyListOrmFields.baseColumnChoiceTypes.toList())

                    val ormObjects = polyListOrmFields.columnNames.map {
                        val columnName = it.columnName.ifEmpty { it.columnChoiceNames[it.columnChoiceTypes.indexOf(chosenType)] }
                        if (columnName.contains("base:")) {
                            val ormObject = OrmEntityOperatorConfig.operator.getEntityField(baseOrmEntity, columnName.replace("base:", "").split("."))
                            if (ormObject == null) {
                                throw RuntimeException("映射出来的是null,$field")
                            }
                            ormObject
                        } else {
                            val (entityValue, entityFieldList) = resolveEntityAndField(columnName, objectNames, objects)
                            val ormObject = OrmEntityOperatorConfig.operator.getEntityField(entityValue, entityFieldList)
                            if (ormObject == null) {
                                throw RuntimeException("映射出来的是null,$field")
                            }
                            ormObject
                        }
                    }
                    DomainAggregateRoot.build(chosenType.java, *ormObjects.toTypedArray())
                }
                // 使用可追踪的List实现，以便在添加/删除元素时通知ORM层
                TrackedAssociationList(baseListOrmObj, baseListOrmFieldList, mappedList,baseListOrmEntity)
                { index,addedDomainEntity ->
                    val ormObjs = DomainAggregateRoot.findOrmObjs(addedDomainEntity)
                    // 先查找withIndexCustomNames中是否存在空字符串，如果存在，则获取对应ormobj 添加到ormBaseList中，如果不存在，则创建一个rmobj，添加到ormBaseList中
                    val choiceTypeIndex = polyListOrmFields.baseColumnChoiceTypes.indexOf(addedDomainEntity::class)
                    if (choiceTypeIndex < 0){
                        throw RuntimeException("没有找到对应的choiceType")
                    }
                    val withIndexCustomNames = polyListOrmFields.columnNames.mapIndexedNotNull { index,value ->
                        val columnName = value.columnName.ifEmpty { value.columnChoiceNames[value.columnChoiceTypes.indexOf(addedDomainEntity::class)] }
                        if (columnName.contains("base:")) {
                            IndexedValue(index,columnName.replace("base:", ""))
                        } else {
                            null
                        }
                    }.sortedBy { it.value.length }
                    val emptyNameIndex = withIndexCustomNames.indexOfFirst { it.value.isEmpty() }
                    val baseOrmObj = if (emptyNameIndex != -1) {
                        ormObjs[emptyNameIndex]
                    } else {
                        // 创建一个新的ORM对象
                        // 这里假设使用第一个ORM对象的类型来创建新实例
                        // 实际实现可能需要根据具体业务需求调整
                        val baseListOrm = OrmEntityOperatorConfig.operator.getEntityField(baseListOrmObj, baseListOrmFieldList)  as List<Any>
                        // 获取list内实际的泛型
                        val baseListOrmType = baseListOrm.javaClass.genericSuperclass as ParameterizedType
                        val baseListOrmGenericType = baseListOrmType.actualTypeArguments[0]
                        // 创建一个新的ORM对象
                        OrmEntityConstructorConfig.constructor.createInstance(baseListOrmGenericType as Class<*>)
                    }
                    if (index >=0){
                        OrmEntityOperatorConfig.operator.addElementToEntityListAt(baseListOrmObj, baseListOrmFieldList, index, baseOrmObj)
                    }else{
                        OrmEntityOperatorConfig.operator.addElementToEntityList(baseListOrmObj, baseListOrmFieldList, baseOrmObj)
                    }

                    withIndexCustomNames.forEach {
                        if (it.value.isNotBlank()){
                            OrmEntityOperatorConfig.operator.setEntityField(baseOrmObj, it.value.split("."), ormObjs[it.index])
                        }
                    }

                    baseOrmObj
                }
            }
            else -> {
                val (entityValue, entityFieldList) = resolveEntityAndField(field.name, objectNames, objects)
                val fieldValue = OrmEntityOperatorConfig.operator.getEntityField(entityValue, entityFieldList)
                // field.type判断是否是基础orm类型，而不是自定义类型
                if (isBasicOrmType(field.type)) {
                    fieldValue
                } else {
                    if (fieldValue == null) {
                        null
                    } else {
                        DomainAggregateRoot.build(field.type, fieldValue)
                    }
                }
            }
        }
        // 将结果存入缓存（仅对非基础类型和List类型）
        if (!isBasicOrmType(field.type) || field.type == List::class.java) {
            fieldValueCache.put(cacheKey, result)
        }
        return result
    }

    override fun setFieldValue(
        pjp: ProceedingJoinPoint,
        field: Field,
        obj: Any,
        value: Any?
    ) {
        // 增加缓存
        // 处理非基础类型情况
        val objects = aggregateRootCache.get(obj)?:throw IllegalStateException("aggregateRootCache not found")
        val objectNames = field.declaringClass.getAnnotation(OrmObject::class.java)?.objectNameList?:arrayOf()
        val ormField: OrmField? = field.getAnnotation(OrmField::class.java)
        val ormFields: OrmFields? = field.getAnnotation(OrmFields::class.java)
        val listOrmFields: ListOrmFields? = field.getAnnotation(ListOrmFields::class.java)
        val polyOrmFields: PolyOrmFields? = field.getAnnotation(PolyOrmFields::class.java)
        val polyListOrmFields: PolyListOrmFields? = field.getAnnotation(PolyListOrmFields::class.java)

        when {
            ormField != null -> {
                val fieldFullName = ormField.columnName
                val (entityValue, entityFieldList) = resolveEntityAndField(fieldFullName, objectNames, objects)
                OrmEntityOperatorConfig.operator.setEntityField(entityValue, entityFieldList, value)
            }
            ormFields != null -> {
                throw UnsupportedOperationException("不支持对 @OrmFields 注解的字段进行设值操作")
            }
            listOrmFields != null -> {
                throw UnsupportedOperationException("不支持对 @ListOrmFields 注解的字段进行设值操作")
            }
            polyOrmFields != null -> {
                throw UnsupportedOperationException("不支持对 @PolyOrmFields 注解的字段进行设值操作")
            }
            polyListOrmFields != null -> {
                throw UnsupportedOperationException("不支持对 @PolyListOrmFields 注解的字段进行设值操作")
            }
            else -> {
                val fieldFullName = field.name
                val (entityValue, entityFieldList) = resolveEntityAndField(fieldFullName, objectNames, objects)
                OrmEntityOperatorConfig.operator.setEntityField(entityValue, entityFieldList, value)
            }
        }
    }

    private fun isBasicOrmType(clazz: Class<*>): Boolean {
        return clazz.isPrimitive ||
                clazz == String::class.java ||
                clazz == java.lang.Boolean::class.java ||
                clazz == java.lang.Character::class.java ||
                clazz == java.lang.Byte::class.java ||
                clazz == java.lang.Short::class.java ||
                clazz == java.lang.Integer::class.java ||
                clazz == java.lang.Long::class.java ||
                clazz == java.lang.Float::class.java ||
                clazz == java.lang.Double::class.java ||
                clazz == java.math.BigDecimal::class.java ||
                clazz == java.math.BigInteger::class.java ||
                clazz == java.util.Date::class.java ||
                clazz == java.sql.Date::class.java ||
                clazz == java.sql.Timestamp::class.java ||
                clazz == java.time.LocalDate::class.java ||
                clazz == java.time.LocalDateTime::class.java ||
                clazz == java.time.LocalTime::class.java ||
                clazz == java.time.Instant::class.java ||
                clazz == java.time.ZonedDateTime::class.java ||
                clazz == java.time.OffsetDateTime::class.java ||
                clazz == java.time.OffsetTime::class.java ||
                clazz.isEnum
    }

    private fun resolveEntityAndField(fieldFullName: String, objectNames: Array<String>, objects: List<Any>): EntityAndField {
        val entityValue: Any
        val entityFieldStr: String
        if (fieldFullName.contains(":")) {
            val objectName = fieldFullName.substringBefore(":")
            //查找objectName是否在objectNames中第几个
            val index = objectNames.indexOf(objectName)
            if (index != -1) {
                entityValue = objects[index]
                entityFieldStr = fieldFullName.substringAfter(":")
            } else {
                val matchResult = pattern.find(objectName) ?: throw RuntimeException("entity name not found")
                val aIndex = matchResult.groupValues[1].toIntOrNull() ?: throw RuntimeException("entity name not found")
                entityValue = objects[aIndex]
                entityFieldStr = fieldFullName.substringAfter(":")
            }
        } else {
            entityValue = objects[0]
            entityFieldStr = fieldFullName
        }
        return EntityAndField(entityValue, entityFieldStr.split("."))
    }

    private data class EntityAndField(val entityValue: Any, val entityFieldList: List<String>)
}

class DomainAggregateRootConstructor: IConstructorBridge {
    override fun createInstance(pjp: ProceedingJoinPoint, args: Array<Any?>) {
        println("createInstance: ${pjp.signature}, args: ${args.contentToString()}")
        val aggregateRootClass = pjp.signature.declaringType
        val ormEntityClasses = aggregateRootToOrmEntityClassCache.get(aggregateRootClass)?:throw IllegalStateException("aggregateRootToOrmEntityClassCache not found")
        val ormEntityList = OrmEntityConstructorConfig.constructor.createInstanceList(ormEntityClasses)
        DomainAggregateRoot.bind(pjp.`this`,*ormEntityList.toTypedArray())
    }
}

class TrackedAssociationList<T>(
    private val baseListOrmObj: Any,
    private val baseListOrmFieldList: List<String>,
    initialList: List<T>,
    baseList: List<Any>,
    val elementAddListener: (Int,Any) -> Any
) : MutableList<T> {

    private val domainEntityList = ArrayList<T>(initialList)

    private val ormBaseList = ArrayList<Any>(baseList)


    override val size: Int
        get() = domainEntityList.size

    override fun isEmpty(): Boolean = domainEntityList.isEmpty()

    override fun contains(element: T): Boolean = domainEntityList.contains(element)

    override fun iterator(): MutableIterator<T> = domainEntityList.iterator()

    override fun listIterator(): MutableListIterator<T> = domainEntityList.listIterator()

    override fun listIterator(index: Int): MutableListIterator<T> = domainEntityList.listIterator(index)

    override fun get(index: Int): T = domainEntityList[index]

    override fun indexOf(element: T): Int = domainEntityList.indexOf(element)

    override fun lastIndexOf(element: T): Int = domainEntityList.lastIndexOf(element)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        return TrackedAssociationList(baseListOrmObj, baseListOrmFieldList, domainEntityList.subList(fromIndex, toIndex), ormBaseList.subList(fromIndex, toIndex),elementAddListener)
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