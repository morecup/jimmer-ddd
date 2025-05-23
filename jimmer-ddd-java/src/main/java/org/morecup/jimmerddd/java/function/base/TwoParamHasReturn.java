package org.morecup.jimmerddd.java.function.base;

import java.io.Serializable;

@FunctionalInterface
public interface TwoParamHasReturn<T, U, R> extends Serializable {
    R apply(T t, U u);
}
