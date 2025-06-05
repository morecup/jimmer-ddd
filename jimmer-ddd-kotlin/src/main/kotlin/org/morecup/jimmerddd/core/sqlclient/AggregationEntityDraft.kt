@file:Suppress("warnings")

package org.morecup.jimmerddd.core.sqlclient

import kotlin.Boolean
import kotlin.Suppress
import org.babyfish.jimmer.Draft
import org.babyfish.jimmer.`internal`.GeneratedBy
import org.babyfish.jimmer.kt.DslScope
import org.babyfish.jimmer.meta.ImmutablePropCategory
import org.babyfish.jimmer.meta.ImmutableType

@DslScope
@GeneratedBy(type = AggregationEntity::class)
public interface AggregationEntityDraft : AggregationEntity, Draft {
    override var idPreLoaded: Boolean

    @GeneratedBy(type = AggregationEntity::class)
    public object `$` {
        public val type: ImmutableType = ImmutableType
                    .newBuilder(
                        "0.9.81",
                        AggregationEntity::class,
                        listOf(

                        ),
                        null
                    )
                    .add(-1, "idPreLoaded", ImmutablePropCategory.SCALAR, Boolean::class.java, false)
                    .build()
    }
}
