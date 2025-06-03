package org.morecup.jimmerddd.core.annotation

enum class AggregationType {
    /** 该属性是聚合的一部分 */
    AGGREGATED,
    /** 该属性不属于聚合 */
    NON_AGGREGATED,
    /** 该属性的ID是聚合的一部分 */
    ID_ONLY,
    /**
     * 默认策略
     * 默认情况下，除了计算属性和transient属性外，其他属性都被视为聚合的一部分，如何是关联属性会被认定为AGGREGATED。
     */
    DEFAULT
}