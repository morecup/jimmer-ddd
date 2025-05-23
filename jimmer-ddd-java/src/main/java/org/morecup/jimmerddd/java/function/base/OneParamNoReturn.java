package org.morecup.jimmerddd.java.function.base;

import java.io.Serializable;

@FunctionalInterface
public interface OneParamNoReturn<T> extends Serializable {
    void accept(T t);
}