package org.morecup.jimmerddd.java.spring.domain;

import org.babyfish.jimmer.ImmutableObjects;
import org.babyfish.jimmer.sql.DraftInterceptor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BaseEntityDraftInterceptor implements DraftInterceptor<BaseEntity, BaseEntityDraft> {

    @Override
    public void beforeSave(BaseEntityDraft draft, @Nullable BaseEntity original) {
        if (!ImmutableObjects.isLoaded(draft, BaseEntityProps.UPDATE_TIME)) {
            draft.setUpdateTime(LocalDateTime.now());
        }
        if (original == null && !ImmutableObjects.isLoaded(draft, BaseEntityProps.CREATE_TIME)) {
            draft.setCreateTime(LocalDateTime.now());
        }
    }
}