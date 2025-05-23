package org.morecup.jimmerddd.java.function.base;

import java.io.Serializable;

@FunctionalInterface
public interface ThreeParamNoReturn<T, U, V> extends Serializable {
    void accept(T t, U u, V v);
}
