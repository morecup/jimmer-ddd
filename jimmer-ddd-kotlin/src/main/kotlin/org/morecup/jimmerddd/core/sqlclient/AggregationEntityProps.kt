@file:Suppress("warnings")
@file:GeneratedBy(type = AggregationEntity::class)

package org.morecup.jimmerddd.core.sqlclient

import kotlin.Boolean
import kotlin.Suppress
import org.babyfish.jimmer.`internal`.GeneratedBy
import org.babyfish.jimmer.kt.toImmutableProp
import org.babyfish.jimmer.meta.TypedProp

@GeneratedBy(type = AggregationEntity::class)
public object AggregationEntityProps {
    public val ID_PRE_LOADED: TypedProp.Scalar<AggregationEntity, Boolean> =
            TypedProp.scalar(AggregationEntity::idPreLoaded.toImmutableProp())
}
