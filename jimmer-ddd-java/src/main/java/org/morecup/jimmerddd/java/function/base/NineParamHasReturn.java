package org.morecup.jimmerddd.java.function.base;

import java.io.Serializable;

@FunctionalInterface
public interface NineParamHasReturn<T, U, V, W, X, Y, Z, A, B, R> extends Serializable {
    R apply(T t, U u, V v, W w, X x, Y y, Z z, A a, B b);
}
