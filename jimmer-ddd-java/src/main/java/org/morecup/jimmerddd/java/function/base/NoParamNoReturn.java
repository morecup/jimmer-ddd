package org.morecup.jimmerddd.java.function.base;

import java.io.Serializable;

@FunctionalInterface
public interface NoParamNoReturn extends Serializable {
    void apply();
}
