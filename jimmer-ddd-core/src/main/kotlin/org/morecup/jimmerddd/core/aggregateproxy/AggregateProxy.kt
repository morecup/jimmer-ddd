package org.morecup.jimmerddd.core.aggregateproxy

import org.morecup.jimmerddd.core.FindByIdFunction
import org.morecup.jimmerddd.core.JimmerDDDConfig
import org.morecup.jimmerddd.core.SaveEntityFunction
import org.morecup.jimmerddd.core.aggregateproxy.multi.MultiEntity
import org.morecup.jimmerddd.core.event.EventManager.publish

open class AggregateProxy<P : Any> @JvmOverloads constructor(
    private val implInterfaceClass: Class<P>,
    private val findByIdFunction: FindByIdFunction = JimmerDDDConfig.getFindByIdFunction(),
    private val saveEntityFunction: SaveEntityFunction = JimmerDDDConfig.getSaveEntityFunction()
) {

    /**
     * 执行lambda并返回需要保存的实体和lazyPublishEvent的事件列表
     * @param base 聚合根实体（任何查询出来的实体都可以）
     * @param implProcessor lambda，入参是根据聚合根实体生成的代理（该代理对象拥有聚合根的所有功能，并且持有changed实体即只含有改变的字段的实体），返回值是lambda执行结果
     */
    fun <T, R> exec(base: T, implProcessor: (P) -> R): ProxyResult<T, R> {
        val context = ProxyContext<T, P>(base, implInterfaceClass, findByIdFunction)
        return context.execute(implProcessor)
    }

    /**
     * 执行lambda并返回lambda执行结果（会自动保存和在保存后自动发布lazyEvent）
     * @param base 聚合根实体（任何查询出来的实体都可以）
     * @param implProcessor lambda，入参是根据聚合根实体生成的代理（该代理对象拥有聚合根的所有功能，并且持有changed实体即只含有改变的字段的实体），返回值是lambda执行结果
     */
    fun <T, R> execAndSave(base: T, implProcessor: (P) -> R): R {
        val context = ProxyContext<T, P>(base, implInterfaceClass, findByIdFunction)
        val (changed, result, lazyPublishEventList) = context.execute(implProcessor)
        saveEntityFunction.invoke(changed!!)
        publish(lazyPublishEventList)
        return result
    }

    /**
     * 执行lambda并返回lambda执行结果和保存后的modified实体（会自动保存和在保存后自动发布lazyEvent）
     * @param base 聚合根实体（任何查询出来的实体都可以）
     * @param implProcessor lambda，入参是根据聚合根实体生成的代理（该代理对象拥有聚合根的所有功能，并且持有changed实体即只含有改变的字段的实体），返回值是lambda执行结果
     */
    fun <T, R> execAndSaveRM(base: T, implProcessor: (P) -> R): ProxyResultRM<T, R> {
        val context = ProxyContext<T, P>(base, implInterfaceClass, findByIdFunction)
        val (changed, result, lazyPublishEventList) = context.execute(implProcessor)
        val modified = saveEntityFunction.invoke(changed!!)
        return ProxyResultRM(modified as T , result,lazyPublishEventList)
    }

    /**
     * 支持多数据库实体映射单个聚合根的场景，其他功能等效execAndSaveRM
     */
    fun <R> execMulti(multiEntity: MultiEntity, implProcessor: (P) -> R): MultiProxyResult<R> {
        val context = ProxyContextMulti<P>(multiEntity.toEntityList(), implInterfaceClass, findByIdFunction)
        return context.execute(implProcessor)
    }

    /**
     * 支持多数据库实体映射单个聚合根的场景，其他功能等效exec
     * @param bases 多个数据库实体
     */
    fun <R> execMulti(vararg bases: Any, implProcessor: (P) -> R): MultiProxyResult<R> {
        val context = ProxyContextMulti<P>(bases.toList(), implInterfaceClass, findByIdFunction)
        return context.execute(implProcessor)
    }

    /**
     * 支持多数据库实体映射单个聚合根的场景，其他功能等效execAndSave
     */
    fun <R> execMultiAndSave(multiEntity: MultiEntity, implProcessor: (P) -> R): R {
        val context = ProxyContextMulti<P>(multiEntity.toEntityList(), implInterfaceClass, findByIdFunction)
        val (changed, result, lazyPublishEventList) = context.execute(implProcessor)
        changed.forEach { saveEntityFunction.invoke(it) }
        publish(lazyPublishEventList)
        return result
    }

    /**
     * 支持多数据库实体映射单个聚合根的场景，其他功能等效execAndSave
     */
    fun <R> execMultiAndSave(vararg bases: Any, implProcessor: (P) -> R): R {
        val context = ProxyContextMulti<P>(bases.toList(), implInterfaceClass, findByIdFunction)
        val (changed, result, lazyPublishEventList) = context.execute(implProcessor)
        changed.forEach { saveEntityFunction.invoke(it) }
        publish(lazyPublishEventList)
        return result
    }
}

data class ProxyResultRM<T,P>(
    /**
     * 保存后返回的modifiedEntity
     */
    val modifiedEntity: T,
    /**
     * lambda执行结果
     */
    val result: P,
    /**
     * 需要懒加载发布的事件列表（聚合内lazyPublishEvent的事件不会立刻发布，会直接返回成列表）
     */
    val lazyPublishEventList: List<Any>
)