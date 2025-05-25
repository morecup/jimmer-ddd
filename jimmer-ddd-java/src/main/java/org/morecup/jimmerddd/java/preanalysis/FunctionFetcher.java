package org.morecup.jimmerddd.java.preanalysis;

import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.morecup.jimmerddd.core.preanalysis.MethodInfo;
import org.morecup.jimmerddd.java.function.MI;

import java.lang.invoke.SerializedLambda;

import static org.morecup.jimmerddd.core.preanalysis.MethodFetcherKt.analysisMethodFetcher;

public class FunctionFetcher {

    public static <T> Fetcher<T> of(Class<T> entityClazz, MethodInfo methodInfo) {
        return analysisMethodFetcher(entityClazz, methodInfo);
    }

    public static <T> Fetcher<T> of(Class<T> entityClazz, SerializedLambda serializedLambda) {
        return analysisMethodFetcher(entityClazz,new MethodInfo(serializedLambda));
    }

    public static <T, T1, R> Fetcher<T> of(Class<T> entityClazz, org.morecup.jimmerddd.java.function.base.OneParamHasReturn<T1, R> lambda) {
        return of(entityClazz, MI.of(lambda));
    }

    public static <T, T1> Fetcher<T> of(Class<T> entityClazz, org.morecup.jimmerddd.java.function.base.OneParamNoReturn<T1> lambda) {
        return of(entityClazz, MI.of(lambda));
    }

    public static <T, T1, T2, R> Fetcher<T> of(Class<T> entityClazz, org.morecup.jimmerddd.java.function.base.TwoParamHasReturn<T1, T2, R> lambda) {
        return of(entityClazz, MI.of(lambda));
    }

    public static <T, T1, T2> Fetcher<T> of(Class<T> entityClazz, org.morecup.jimmerddd.java.function.base.TwoParamNoReturn<T1, T2> lambda) {
        return of(entityClazz, MI.of(lambda));
    }

    public static <T, T1, T2, T3, R> Fetcher<T> of(Class<T> entityClazz, org.morecup.jimmerddd.java.function.base.ThreeParamHasReturn<T1, T2, T3, R> lambda) {
        return of(entityClazz, MI.of(lambda));
    }

    public static <T, T1, T2, T3> Fetcher<T> of(Class<T> entityClazz, org.morecup.jimmerddd.java.function.base.ThreeParamNoReturn<T1, T2, T3> lambda) {
        return of(entityClazz, MI.of(lambda));
    }

    public static <T, T1, T2, T3, T4, R> Fetcher<T> of(Class<T> entityClazz, org.morecup.jimmerddd.java.function.base.FourParamHasReturn<T1, T2, T3, T4, R> lambda) {
        return of(entityClazz, MI.of(lambda));
    }

    public static <T, T1, T2, T3, T4> Fetcher<T> of(Class<T> entityClazz, org.morecup.jimmerddd.java.function.base.FourParamNoReturn<T1, T2, T3, T4> lambda) {
        return of(entityClazz, MI.of(lambda));
    }

    public static <T, T1, T2, T3, T4, T5, R> Fetcher<T> of(Class<T> entityClazz, org.morecup.jimmerddd.java.function.base.FiveParamHasReturn<T1, T2, T3, T4, T5, R> lambda) {
        return of(entityClazz, MI.of(lambda));
    }

    public static <T, T1, T2, T3, T4, T5> Fetcher<T> of(Class<T> entityClazz, org.morecup.jimmerddd.java.function.base.FiveParamNoReturn<T1, T2, T3, T4, T5> lambda) {
        return of(entityClazz, MI.of(lambda));
    }

    public static <T, T1, T2, T3, T4, T5, T6, R> Fetcher<T> of(Class<T> entityClazz, org.morecup.jimmerddd.java.function.base.SixParamHasReturn<T1, T2, T3, T4, T5, T6, R> lambda) {
        return of(entityClazz, MI.of(lambda));
    }

    public static <T, T1, T2, T3, T4, T5, T6> Fetcher<T> of(Class<T> entityClazz, org.morecup.jimmerddd.java.function.base.SixParamNoReturn<T1, T2, T3, T4, T5, T6> lambda) {
        return of(entityClazz, MI.of(lambda));
    }

    public static <T, T1, T2, T3, T4, T5, T6, T7, R> Fetcher<T> of(Class<T> entityClazz, org.morecup.jimmerddd.java.function.base.SevenParamHasReturn<T1, T2, T3, T4, T5, T6, T7, R> lambda) {
        return of(entityClazz, MI.of(lambda));
    }

    public static <T, T1, T2, T3, T4, T5, T6, T7> Fetcher<T> of(Class<T> entityClazz, org.morecup.jimmerddd.java.function.base.SevenParamNoReturn<T1, T2, T3, T4, T5, T6, T7> lambda) {
        return of(entityClazz, MI.of(lambda));
    }

    public static <T, T1, T2, T3, T4, T5, T6, T7, T8, R> Fetcher<T> of(Class<T> entityClazz, org.morecup.jimmerddd.java.function.base.EightParamHasReturn<T1, T2, T3, T4, T5, T6, T7, T8, R> lambda) {
        return of(entityClazz, MI.of(lambda));
    }

    public static <T, T1, T2, T3, T4, T5, T6, T7, T8> Fetcher<T> of(Class<T> entityClazz, org.morecup.jimmerddd.java.function.base.EightParamNoReturn<T1, T2, T3, T4, T5, T6, T7, T8> lambda) {
        return of(entityClazz, MI.of(lambda));
    }

    public static <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, R> Fetcher<T> of(Class<T> entityClazz, org.morecup.jimmerddd.java.function.base.NineParamHasReturn<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> lambda) {
        return of(entityClazz, MI.of(lambda));
    }

    public static <T, T1, T2, T3, T4, T5, T6, T7, T8, T9> Fetcher<T> of(Class<T> entityClazz, org.morecup.jimmerddd.java.function.base.NineParamNoReturn<T1, T2, T3, T4, T5, T6, T7, T8, T9> lambda) {
        return of(entityClazz, MI.of(lambda));
    }

    public static <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> Fetcher<T> of(Class<T> entityClazz, org.morecup.jimmerddd.java.function.base.TenParamHasReturn<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> lambda) {
        return of(entityClazz, MI.of(lambda));
    }

    public static <T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Fetcher<T> of(Class<T> entityClazz, org.morecup.jimmerddd.java.function.base.TenParamNoReturn<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> lambda) {
        return of(entityClazz, MI.of(lambda));
    }

    public static <T, R> Fetcher<T> of(Class<T> entityClazz, org.morecup.jimmerddd.java.function.base.NoParamHasReturn<R> lambda) {
        return of(entityClazz, MI.of(lambda));
    }

    public static <T> Fetcher<T> of(Class<T> entityClazz, org.morecup.jimmerddd.java.function.base.NoParamNoReturn lambda) {
        return of(entityClazz, MI.of(lambda));
    }
}