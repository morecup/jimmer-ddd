package org.morecup.jimmerddd.core.sqlclient;

import org.babyfish.jimmer.Draft;
import org.babyfish.jimmer.internal.GeneratedBy;
import org.babyfish.jimmer.lang.OldChain;
import org.babyfish.jimmer.meta.ImmutablePropCategory;
import org.babyfish.jimmer.meta.ImmutableType;

import java.util.Collections;

@GeneratedBy(
        type = AggregationEntity.class
)
public interface AggregationEntityDraft extends AggregationEntity, Draft {
    Producer $ = Producer.INSTANCE;

    @OldChain
    AggregationEntityDraft setIdPreLoaded(Boolean idPreLoaded);

    @GeneratedBy(
            type = AggregationEntity.class
    )
    class Producer {
        static final Producer INSTANCE = new Producer();

        public static final ImmutableType TYPE = ImmutableType
            .newBuilder(
                "0.9.81",
                AggregationEntity.class,
                Collections.emptyList(),
                null
            )
            .add(-1, "idPreLoaded", ImmutablePropCategory.SCALAR, Boolean.class, true)
            .build();

        private Producer() {
        }
    }
}
