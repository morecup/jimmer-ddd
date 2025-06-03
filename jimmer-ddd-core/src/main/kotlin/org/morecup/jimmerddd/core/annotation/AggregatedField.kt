package org.morecup.jimmerddd.core.annotation


@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class AggregatedField(
    /**
     * 聚合类型
     * 用于指定当前属性是否属于聚合的一部分。
     */
    val type: AggregationType = AggregationType.AGGREGATED,
    /**
     * 是否懒加载
     * 用于指定当前属性是否需要懒加载。
     */
    val lazy: Boolean = false,
)