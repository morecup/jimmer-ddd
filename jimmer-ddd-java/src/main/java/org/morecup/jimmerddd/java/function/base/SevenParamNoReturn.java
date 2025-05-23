package org.morecup.jimmerddd.java.function.base;

import java.io.Serializable;

@FunctionalInterface
public interface SevenParamNoReturn<T, U, V, W, X, Y, Z> extends Serializable {
    void accept(T t, U u, V v, W w, X x, Y y, Z z);
}
