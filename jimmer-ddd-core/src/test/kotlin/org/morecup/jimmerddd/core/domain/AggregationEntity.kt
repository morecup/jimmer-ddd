package org.morecup.jimmerddd.core.domain

import org.babyfish.jimmer.runtime.ImmutableSpi
import org.babyfish.jimmer.sql.MappedSuperclass
import org.babyfish.jimmer.sql.Transient

@MappedSuperclass
interface AggregationEntity {
    /**
     * id是否已经提前填充过（用于提前生成了的id，但是依然是insert的场景，主要为了BaseEntityDraftInterceptor）
     */
    @Transient
    val idPreLoaded: Boolean

    fun checkIsInsertOrUpdate(): Boolean {
        val spi = this as ImmutableSpi
        val type = spi.__type()
        return if (!spi.__isLoaded(type.idProp.id)){
            true
        }else if (spi.__isLoaded("idPreLoaded")&& spi.__get("idPreLoaded") as Boolean){
            true
        }else{
            false
        }
    }
}