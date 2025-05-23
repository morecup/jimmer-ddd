package org.morecup.jimmerddd.core.annotation

annotation class Lazy(
    val prepareLoadMode: PrepareLoadMode = PrepareLoadMode.Unload
)