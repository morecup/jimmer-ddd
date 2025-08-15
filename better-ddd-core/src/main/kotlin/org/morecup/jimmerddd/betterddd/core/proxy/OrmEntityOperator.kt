package org.morecup.jimmerddd.betterddd.core.proxy

import kotlin.reflect.KClass


object OrmEntityOperatorConfig {
    var operator: IOrmEntityOperator = DefaultOrmEntityOperator()
}

interface IOrmEntityOperator {
    fun getEntityField(entity:Any,fieldList:List<String>):Any?
    fun setEntityField(entity:Any,fieldList:List<String>,value:Any?)
}

class DefaultOrmEntityOperator: IOrmEntityOperator {
    override fun getEntityField(entity: Any, fieldList: List<String>): Any? {
        println("entity:$entity, fieldPathStr:$fieldList")
        return null
    }

    override fun setEntityField(entity: Any, fieldList: List<String>, value: Any?) {
        println("entity:$entity, fieldPathStr:$fieldList,value:$value")
    }

}

object OrmEntityConstructorConfig {
    var constructor: IOrmEntityConstructor = DefaultOrmEntityConstructor()
}

interface IOrmEntityConstructor {
    fun createInstance(ormEntityClassList: List<Class<*>>,domainAggregateInstance:Any)
}
class DefaultOrmEntityConstructor: IOrmEntityConstructor {
    override fun createInstance(ormEntityClassList: List<Class<*>>,domainAggregateInstance:Any) {
        println("createInstance: ormEntityClassList:$ormEntityClassList,domainAggregateInstance:$domainAggregateInstance")
    }
}
