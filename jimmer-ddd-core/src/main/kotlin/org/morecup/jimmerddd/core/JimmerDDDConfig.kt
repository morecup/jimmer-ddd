package org.morecup.jimmerddd.core

import org.babyfish.jimmer.sql.fetcher.Fetcher
import org.morecup.jimmerddd.core.aggregateproxy.GlobalContext.nullDraftContext

typealias FindByIdFunction = (Fetcher<*>, Any) -> Any?
typealias SaveEntityFunction = (Any) -> Any
typealias IdGeneratorFunction = (entityType: Class<*>) -> Any?
typealias EventPublishFunction = (Any) -> Unit

object JimmerDDDConfig {

    private var findByIdFunction:FindByIdFunction? = null
    private var saveEntityFunction: SaveEntityFunction? = null
    private var idGeneratorFunction:IdGeneratorFunction? = null
    private var eventPublishFunction: EventPublishFunction? = null

    @JvmStatic
    fun setFindByIdFunction(findByIdFunction: FindByIdFunction) {
        this.findByIdFunction = findByIdFunction
    }

    @JvmStatic
    fun setSaveEntityFunction(saveEntityFunction: SaveEntityFunction) {
        this.saveEntityFunction = saveEntityFunction
    }

    //    @JvmStatic
    internal fun getFindByIdFunction():FindByIdFunction{
        if (findByIdFunction == null) {
            throw JimmerDDDException("JimmerDDDConfig.findByIdFunction未配置，请配置成对应jimmer sqlClient findById的逻辑")
        }
        return findByIdFunction!!
    }

    internal fun getSaveEntityFunction(): SaveEntityFunction {
        if (saveEntityFunction == null) {
            throw JimmerDDDException("JimmerDDDConfig.saveEntityFunction未配置，请配置成对应jimmer sqlClient saveEntity的逻辑")
        }
        return saveEntityFunction!!
    }

    @JvmStatic
    fun setIdGeneratorFunction(idGeneratorFunction: IdGeneratorFunction) {
        this.idGeneratorFunction = idGeneratorFunction
    }

    internal fun getIdGeneratorFunction(): IdGeneratorFunction {
        if (idGeneratorFunction == null) {
            throw JimmerDDDException("JimmerDDDConfig.userIdGenerator未配置，请配置成对应jimmer sqlClient userIdGenerator的逻辑")
        }
        return idGeneratorFunction!!
    }

    @JvmStatic
    fun setEventPublishFunction(eventPublishFunction: EventPublishFunction) {
        this.eventPublishFunction = eventPublishFunction
    }

    internal fun getEventPublishFunction(): EventPublishFunction {
        if (eventPublishFunction == null) {
            throw JimmerDDDException("JimmerDDDConfig.eventPublishFunction未配置，请配置成对应jimmer sqlClient getEventPublishFunction的逻辑")
        }
        return eventPublishFunction!!
    }

    internal fun publishEvent(event: Any) {
        nullDraftContext {
            getEventPublishFunction().invoke(event)
        }
    }
}