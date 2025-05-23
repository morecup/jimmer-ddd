package org.morecup.jimmerddd.java.function.base;

import java.io.Serializable;

@FunctionalInterface
public interface EightParamNoReturn<T, U, V, W, X, Y, Z, A> extends Serializable {
    void accept(T t, U u, V v, W w, X x, Y y, Z z, A a);
}
