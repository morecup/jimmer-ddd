package org.morecup.jimmerddd.java.spring.domain.goods;

import org.babyfish.jimmer.sql.Entity;
import org.morecup.jimmerddd.java.spring.domain.BaseEntity;

@Entity
public interface Goods extends BaseEntity {
    String name();
    String nowAddress();
}

