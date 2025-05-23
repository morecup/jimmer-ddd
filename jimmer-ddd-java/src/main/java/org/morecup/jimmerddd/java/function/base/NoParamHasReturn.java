package org.morecup.jimmerddd.java.function.base;

import java.io.Serializable;

@FunctionalInterface
public interface NoParamHasReturn<T> extends Serializable {
    T get();
}