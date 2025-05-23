package org.morecup.jimmerddd.java.function.base;

import java.io.Serializable;

@FunctionalInterface
public interface ThreeParamHasReturn<T, U, V, R> extends Serializable {
    R apply(T t, U u, V v);
}
