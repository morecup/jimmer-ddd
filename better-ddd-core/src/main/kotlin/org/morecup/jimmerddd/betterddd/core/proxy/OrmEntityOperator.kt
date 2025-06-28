package org.morecup.jimmerddd.betterddd.core.proxy


object OrmEntityOperatorConfig {
    var operator: IOrmEntityOperator = DefaultOrmEntityOperator()
}

interface IOrmEntityOperator {
    fun getEntityField(entity:Any,fieldPathStr:String):Any?
    fun setEntityField(entity:Any,fieldPathStr:String,value:Any?)
}

class DefaultOrmEntityOperator: IOrmEntityOperator {
    override fun getEntityField(entity: Any, fieldPathStr: String): Any? {
        println("entity:$entity, fieldPathStr:$fieldPathStr")
        return null
    }

    override fun setEntityField(entity: Any, fieldPathStr: String, value: Any?) {
        println("entity:$entity, fieldPathStr:$fieldPathStr,value:$value")
    }

}