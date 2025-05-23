package org.morecup.jimmerddd.java.function.base;

import java.io.Serializable;

@FunctionalInterface
public interface FiveParamNoReturn<T, U, V, W, X> extends Serializable {
    void accept(T t, U u, V v, W w, X x);
}
