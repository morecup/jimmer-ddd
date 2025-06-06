package org.morecup.jimmerddd.core.sqlclient;

import org.babyfish.jimmer.internal.GeneratedBy;
import org.babyfish.jimmer.meta.ImmutableType;
import org.babyfish.jimmer.meta.TypedProp;
import org.babyfish.jimmer.sql.ast.table.Props;
import org.babyfish.jimmer.sql.ast.table.PropsFor;

@GeneratedBy(
        type = AggregationEntity.class
)
@PropsFor(AggregationEntity.class)
public interface AggregationEntityProps extends Props {
    TypedProp.Scalar<AggregationEntity, Boolean> ID_PRE_LOADED = 
        TypedProp.scalar(ImmutableType.get(AggregationEntity.class).getProp("idPreLoaded"));
}
