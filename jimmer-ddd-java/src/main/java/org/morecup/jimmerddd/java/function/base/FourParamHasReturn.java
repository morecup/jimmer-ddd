package org.morecup.jimmerddd.java.function.base;

import java.io.Serializable;

@FunctionalInterface
public interface FourParamHasReturn<T, U, V, W, R> extends Serializable {
    R apply(T t, U u, V v, W w);
}
