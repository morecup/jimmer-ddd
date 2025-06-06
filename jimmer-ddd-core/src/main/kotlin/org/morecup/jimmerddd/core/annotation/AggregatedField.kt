package org.morecup.jimmerddd.core.annotation


@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY,AnnotationTarget.FUNCTION)
annotation class AggregatedField(
    /**
     * 聚合类型
     * 用于指定当前属性是否属于聚合的一部分。
     * 如果聚合根属性上未标明AggregatedField注解，则启用默认策略 DEFAULT。
     */
    val type: AggregationType = AggregationType.AGGREGATED,
    /**
     * 是否懒加载
     * 用于指定当前属性是否需要懒加载。
     */
    val lazy: Boolean = false,
)