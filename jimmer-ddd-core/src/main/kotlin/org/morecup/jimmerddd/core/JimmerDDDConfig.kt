package org.morecup.jimmerddd.core

import org.babyfish.jimmer.sql.fetcher.Fetcher
import org.babyfish.jimmer.sql.meta.UserIdGenerator

typealias FindByIdFunction = (Fetcher<*>, Any) -> Any?
typealias SaveEntityFunction = (Any) -> Any

object JimmerDDDConfig {

    private var findByIdFunction:FindByIdFunction? = null
    private var saveEntityFunction: SaveEntityFunction? = null
    private var userIdGenerator:UserIdGenerator<*>? = null

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
    fun setUserIdGenerator(userIdGenerator: UserIdGenerator<*>) {
        this.userIdGenerator = userIdGenerator
    }

    internal fun getUserIdGenerator(): UserIdGenerator<*> {
        if (userIdGenerator == null) {
            throw JimmerDDDException("JimmerDDDConfig.userIdGenerator未配置，请配置成对应jimmer sqlClient userIdGenerator的逻辑")
        }
        return userIdGenerator!!
    }
}