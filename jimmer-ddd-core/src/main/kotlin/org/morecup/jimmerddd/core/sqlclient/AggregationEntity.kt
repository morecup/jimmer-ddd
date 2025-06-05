package org.morecup.jimmerddd.core.sqlclient

import org.babyfish.jimmer.runtime.ImmutableSpi
import org.babyfish.jimmer.sql.MappedSuperclass
import org.babyfish.jimmer.sql.Transient

/**
 * 也可以不继承这个类，但需要声明相似的idPreLoaded属性，否则只能会将无id的当作insert处理
 * 继承这个类或者声明idPreLoaded属性，可以通过设置idPreLoaded为true，将有id的也当作update处理
 */
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