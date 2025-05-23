package org.morecup.jimmerddd.java.function.base;

import java.io.Serializable;

@FunctionalInterface
public interface TwoParamNoReturn<T, U> extends Serializable {
    void accept(T t, U u);
}