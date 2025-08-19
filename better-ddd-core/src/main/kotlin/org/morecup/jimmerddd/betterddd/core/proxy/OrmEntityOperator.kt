package org.morecup.jimmerddd.betterddd.core.proxy

import kotlin.reflect.KClass


object OrmEntityOperatorConfig {
    var operator: IOrmEntityOperator = DefaultOrmEntityOperator()
}

interface IOrmEntityOperator {
    fun getEntityField(entity:Any,fieldList:List<String>):Any?
    fun setEntityField(entity:Any,fieldList:List<String>,value:Any?)
    fun addElementToEntityList(entity: Any, fieldList: List<String>, element: Any)
    fun addElementToEntityListAt(entity: Any, fieldList: List<String>, index: Int, element: Any)
    fun removeElementFromEntityList(entity: Any, fieldList: List<String>, element: Any)
}

class DefaultOrmEntityOperator: IOrmEntityOperator {
    override fun getEntityField(entity: Any, fieldList: List<String>): Any? {
        println("entity:$entity, fieldPathStr:$fieldList")
        return null
    }

    override fun setEntityField(entity: Any, fieldList: List<String>, value: Any?) {
        println("entity:$entity, fieldPathStr:$fieldList,value:$value")
    }

    override fun addElementToEntityList(entity: Any, fieldList: List<String>, element: Any) {
        println("向实体添加关联元素: entity=$entity, fieldPath=$fieldList, element=$element")
    }

    override fun addElementToEntityListAt(entity: Any, fieldList: List<String>, index: Int, element: Any) {
        println("向实体添加关联元素: entity=$entity, fieldPath=$fieldList, index=$index, element=$element")
    }

    override fun removeElementFromEntityList(entity: Any, fieldList: List<String>, element: Any) {
        println("从实体移除关联元素: entity=$entity, fieldPath=$fieldList, element=$element")
    }
}

object OrmEntityConstructorConfig {
    var constructor: IOrmEntityConstructor = DefaultOrmEntityConstructor()
}

interface IOrmEntityConstructor {
    fun createInstanceList(ormEntityClassList: List<Class<*>>):List<Any>
    fun createInstance(ormEntityClass: Class<*>): Any
}
class DefaultOrmEntityConstructor: IOrmEntityConstructor {
    override fun createInstanceList(ormEntityClassList: List<Class<*>>):List<Any> {
        println("createInstance: ormEntityClassList:$ormEntityClassList")
        return emptyList()
    }
    override fun createInstance(ormEntityClass: Class<*>): Any {
        println("createInstance: ormEntityClass:$ormEntityClass")
        return ormEntityClass.newInstance()
    }
}
