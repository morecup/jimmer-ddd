package org.morecup.jimmerddd.java.function.base;

import java.io.Serializable;

@FunctionalInterface
public interface NineParamNoReturn<T, U, V, W, X, Y, Z, A, B> extends Serializable {
    void accept(T t, U u, V v, W w, X x, Y y, Z z, A a, B b);
}
