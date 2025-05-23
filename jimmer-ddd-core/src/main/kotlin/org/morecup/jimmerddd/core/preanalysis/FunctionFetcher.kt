package org.morecup.jimmerddd.core.preanalysis

import org.babyfish.jimmer.sql.fetcher.impl.FetcherImpl
import org.babyfish.jimmer.sql.fetcher.impl.FetcherImplementor
import org.morecup.jimmerddd.core.annotation.AggregatedField
import org.morecup.jimmerddd.core.annotation.AggregationType
import org.objectweb.asm.Type
import kotlin.collections.plus
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod

fun <T : Any> analysisFunctionFetcher(function: KFunction<*>, entityClazz: KClass<T>): FetcherImplementor<T> {
    val fetcherImpl: FetcherImpl<T> = FetcherImpl(entityClazz.java)
    val analyzeMethods: MutableSet<AsmMethodInfo> = ClassMethodAnalyzer.analyzeMethods(function.javaMethod!!)
    val neededGetFieldNameSet = analyzeMethods.filter {
//        it.ownerClass == function.javaMethod?.declaringClass?.name&&
                it.desc.startsWith("()")&&
                it.desc!="()Z" }.map {
                    if (it.ownerClass.endsWith("Draft")){ it.ownerClass = it.ownerClass.substring(0, it.ownerClass.length - "Draft".length)}
                    if (it.name.startsWith("get")){ it.name = it.name.substring(3).replaceFirstChar { it.lowercase() }}
                    if (it.desc.endsWith("Draft;")){ it.desc = it.desc.substring(0, it.desc.length - "Draft;".length) + ";"}
                    it
                }.toHashSet()

    return asmMethodInfoToFetcher(fetcherImpl, neededGetFieldNameSet)
}

private fun <T> asmMethodInfoToFetcher(baseFetcher:FetcherImplementor<T>,asmMethodInfoSet: HashSet<AsmMethodInfo>,loadedClassList:List<Class<*>> = arrayListOf()): FetcherImplementor<T> {
    var fetcher:FetcherImplementor<T> = baseFetcher
    for (prop in fetcher.immutableType.props.values) {
        val asmMethodInfo = AsmMethodInfo(fetcher.immutableType.javaClass.name,prop.name, Type.getMethodDescriptor(Type.getType(prop.returnClass)))

        if (!asmMethodInfoSet.contains(asmMethodInfo)) {
            continue
        }
        val annotations = prop.annotations
        val aggregatedField = annotations.filterIsInstance<AggregatedField>().firstOrNull()
//        val lazy = annotations.filterIsInstance<Lazy>().firstOrNull()
//        if (lazy != null && lazy.prepareLoadMode == PrepareLoadMode.Unload) {
//            continue
//        }
        if (aggregatedField == null|| aggregatedField.type == AggregationType.AGGREGATED) {
            if (prop.targetType!=null){
                if (!loadedClassList.contains(prop.targetType.javaClass)){
                    val fields = asmMethodInfoToFetcher(
                        FetcherImpl(prop.targetType.javaClass),
                        asmMethodInfoSet,
                        loadedClassList + fetcher.immutableType.javaClass
                    )
                    fetcher = fetcher.add(prop.name,fields)
                }else{
                    continue
                }
            }else{
                fetcher = fetcher.add(prop.name)
            }
        }else if (aggregatedField.type == AggregationType.ID_ONLY){
            fetcher = fetcher.add(prop.name)
        }else if (aggregatedField.type == AggregationType.NON_AGGREGATED){
            continue
        }
    }
    return fetcher
}