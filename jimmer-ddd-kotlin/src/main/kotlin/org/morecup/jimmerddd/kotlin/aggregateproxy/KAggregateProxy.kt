package org.morecup.jimmerddd.kotlin.aggregateproxy

import org.morecup.jimmerddd.core.FindByIdFunction
import org.morecup.jimmerddd.core.JimmerDDDConfig
import org.morecup.jimmerddd.core.SaveEntityFunction
import org.morecup.jimmerddd.core.aggregateproxy.AggregateProxy
import kotlin.reflect.KClass

class KAggregateProxy<P : Any> @JvmOverloads constructor(
    private val implInterfaceClass: KClass<P>,
    private val findByIdFunction: FindByIdFunction = JimmerDDDConfig.getFindByIdFunction(),
    private val saveEntityFunction: SaveEntityFunction = JimmerDDDConfig.getSaveEntityFunction()
): AggregateProxy<P>(implInterfaceClass.java,findByIdFunction,saveEntityFunction){

}