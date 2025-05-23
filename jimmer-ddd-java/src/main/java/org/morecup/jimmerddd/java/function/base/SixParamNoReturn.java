package org.morecup.jimmerddd.java.function.base;

import java.io.Serializable;

@FunctionalInterface
public interface SixParamNoReturn<T, U, V, W, X, Y> extends Serializable {
    void accept(T t, U u, V v, W w, X x, Y y);
}
