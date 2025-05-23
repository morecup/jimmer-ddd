package org.morecup.jimmerddd.java.function.base;

import java.io.Serializable;

@FunctionalInterface
public interface FiveParamHasReturn<T, U, V, W, X, R> extends Serializable {
    R apply(T t, U u, V v, W w, X x);
}
