package org.morecup.jimmerddd.java.function.base;

import java.io.Serializable;

@FunctionalInterface
public interface FourParamNoReturn<T, U, V, W> extends Serializable {
    void accept(T t, U u, V v, W w);
}
