package org.morecup.jimmerddd.core.annotation


@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class AggregatedField(
    val type: AggregationType = AggregationType.ID_ONLY
)