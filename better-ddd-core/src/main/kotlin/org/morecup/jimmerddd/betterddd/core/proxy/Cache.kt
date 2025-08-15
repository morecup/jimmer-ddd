package org.morecup.jimmerddd.betterddd.core.proxy

import org.morecup.jimmerddd.betterddd.core.util.ConcurrentWeakHashMap

val aggregateRootToOrmEntityClassCache = ConcurrentWeakHashMap<Class<*>, List<Class<*>>>()
