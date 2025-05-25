package org.morecup.jimmerddd.java.function;

import org.morecup.jimmerddd.core.preanalysis.MethodInfo;
import org.morecup.jimmerddd.java.function.base.ThreeParamHasReturn;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;

public class MI {
    public static <T, U, V, R> MethodInfo of(ThreeParamHasReturn<T, U, V, R> lambda){
        return resolveMethodInfo(lambda);
    }
    public static <T1, T2, T3, T4, T5, R> MethodInfo of(org.morecup.jimmerddd.java.function.base.FiveParamHasReturn<T1, T2, T3, T4, T5, R> lambda) {
        return resolveMethodInfo(lambda);
    }
    public static <T1, T2, T3, T4, R> MethodInfo of(org.morecup.jimmerddd.java.function.base.FourParamHasReturn<T1, T2, T3, T4, R> lambda) {
        return resolveMethodInfo(lambda);
    }
    public static <T1, T2, T3, T4, T5, T6, R> MethodInfo of(org.morecup.jimmerddd.java.function.base.SixParamHasReturn<T1, T2, T3, T4, T5, T6, R> lambda) {
        return resolveMethodInfo(lambda);
    }
    public static <T1, T2, T3, T4, T5, T6, T7, R> MethodInfo of(org.morecup.jimmerddd.java.function.base.SevenParamHasReturn<T1, T2, T3, T4, T5, T6, T7, R> lambda) {
        return resolveMethodInfo(lambda);
    }
    public static <T1, T2, T3, T4, T5, T6, T7, T8, R> MethodInfo of(org.morecup.jimmerddd.java.function.base.EightParamHasReturn<T1, T2, T3, T4, T5, T6, T7, T8, R> lambda) {
        return resolveMethodInfo(lambda);
    }
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> MethodInfo of(org.morecup.jimmerddd.java.function.base.NineParamHasReturn<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> lambda) {
        return resolveMethodInfo(lambda);
    }
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> MethodInfo of(org.morecup.jimmerddd.java.function.base.TenParamHasReturn<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> lambda) {
        return resolveMethodInfo(lambda);
    }
    public static <R> MethodInfo of(org.morecup.jimmerddd.java.function.base.NoParamHasReturn<R> lambda) {
        return resolveMethodInfo(lambda);
    }
    public static <T, R> MethodInfo of(org.morecup.jimmerddd.java.function.base.OneParamHasReturn<T, R> lambda) {
        return resolveMethodInfo(lambda);
    }
    public static <T1, T2, R> MethodInfo of(org.morecup.jimmerddd.java.function.base.TwoParamHasReturn<T1, T2, R> lambda) {
        return resolveMethodInfo(lambda);
    }
    public static <T1, T2, T3> MethodInfo of(org.morecup.jimmerddd.java.function.base.ThreeParamNoReturn<T1, T2, T3> lambda) {
        return resolveMethodInfo(lambda);
    }
    public static <T1, T2> MethodInfo of(org.morecup.jimmerddd.java.function.base.TwoParamNoReturn<T1, T2> lambda) {
        return resolveMethodInfo(lambda);
    }
    public static <T> MethodInfo of(org.morecup.jimmerddd.java.function.base.OneParamNoReturn<T> lambda) {
        return resolveMethodInfo(lambda);
    }
    public static MethodInfo of(org.morecup.jimmerddd.java.function.base.NoParamNoReturn lambda) {
        return resolveMethodInfo(lambda);
    }
    public static <T1, T2, T3, T4> MethodInfo of(org.morecup.jimmerddd.java.function.base.FourParamNoReturn<T1, T2, T3, T4> lambda) {
        return resolveMethodInfo(lambda);
    }
    public static <T1, T2, T3, T4, T5> MethodInfo of(org.morecup.jimmerddd.java.function.base.FiveParamNoReturn<T1, T2, T3, T4, T5> lambda) {
        return resolveMethodInfo(lambda);
    }
    public static <T1, T2, T3, T4, T5, T6> MethodInfo of(org.morecup.jimmerddd.java.function.base.SixParamNoReturn<T1, T2, T3, T4, T5, T6> lambda) {
        return resolveMethodInfo(lambda);
    }
    public static <T1, T2, T3, T4, T5, T6, T7> MethodInfo of(org.morecup.jimmerddd.java.function.base.SevenParamNoReturn<T1, T2, T3, T4, T5, T6, T7> lambda) {
        return resolveMethodInfo(lambda);
    }
    public static <T1, T2, T3, T4, T5, T6, T7, T8> MethodInfo of(org.morecup.jimmerddd.java.function.base.EightParamNoReturn<T1, T2, T3, T4, T5, T6, T7, T8> lambda) {
        return resolveMethodInfo(lambda);
    }
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> MethodInfo of(org.morecup.jimmerddd.java.function.base.NineParamNoReturn<T1, T2, T3, T4, T5, T6, T7, T8, T9> lambda) {
        return resolveMethodInfo(lambda);
    }
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> MethodInfo of(org.morecup.jimmerddd.java.function.base.TenParamNoReturn<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> lambda) {
        return resolveMethodInfo(lambda);
    }

    public static MethodInfo resolveMethodInfo(Serializable lambda) {
        SerializedLambda serializedLambda = SL.resolveSerializedLambda(lambda);
        return new MethodInfo(serializedLambda);
    }
}
