package org.morecup.jimmerddd.core

import org.babyfish.jimmer.sql.fetcher.Fetcher

typealias FindByIdFunction = (Fetcher<*>, Any) -> Any?

object JimmerDDDConfig {

    private var findByIdFunction:FindByIdFunction? = null

    @JvmStatic
    fun setFindByIdFunction(findByIdFunction: FindByIdFunction) {
        this.findByIdFunction = findByIdFunction
    }

//    @JvmStatic
    fun getFindByIdFunction():FindByIdFunction{
        if (findByIdFunction == null) {
            throw JimmerDDDException("JimmerDDDConfig.findByIdFunction未配置，请配置成对应jimmer sqlClient findById的逻辑")
        }
        return findByIdFunction!!
    }

}