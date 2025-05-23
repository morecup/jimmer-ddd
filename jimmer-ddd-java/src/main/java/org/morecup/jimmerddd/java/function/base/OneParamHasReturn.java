package org.morecup.jimmerddd.java.function.base;

import java.io.Serializable;

@FunctionalInterface
public interface OneParamHasReturn<T, R> extends Serializable {
    R apply(T t);
}
