package org.morecup.jimmerddd.betterddd.core.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class DomainEntity(
    val objectNameList: Array<String> = [],
)