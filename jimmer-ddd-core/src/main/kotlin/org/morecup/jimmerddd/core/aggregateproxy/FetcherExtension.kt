@file:JvmName("ImmutableSpiExtend")
package org.morecup.jimmerddd.core.aggregateproxy

import org.babyfish.jimmer.sql.fetcher.Fetcher

fun <T> Fetcher<T>.isIdOnly(): Boolean {
    return fieldMap.size == 1 && fieldMap.values.first().prop.isId
}