package org.morecup.jimmerddd.core.annotation

enum class AggregationType {
    /** 该属性是聚合的一部分（默认行为） */
    AGGREGATED,
    /** 该属性不属于聚合 */
    NON_AGGREGATED,
    /** 该属性的ID是聚合的一部分 */
    ID_ONLY
}