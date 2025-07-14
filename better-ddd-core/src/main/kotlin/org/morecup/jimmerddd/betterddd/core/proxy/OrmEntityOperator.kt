package org.morecup.jimmerddd.betterddd.core.proxy


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