package org.morecup.jimmerddd.java.function;

import org.morecup.jimmerddd.core.JimmerDDDException;
import org.morecup.jimmerddd.java.function.base.ThreeParamHasReturn;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SL {
    private static final Map<Serializable, SerializedLambda> SerializableCache = new ConcurrentHashMap<>();
    
    public static <T, U, V, R> SerializedLambda of(ThreeParamHasReturn<T, U, V, R> lambda){
        return resolveSerializedLambda(lambda);
    }
    public static <T1, T2, T3, T4, T5, R> SerializedLambda of(org.morecup.jimmerddd.java.function.base.FiveParamHasReturn<T1, T2, T3, T4, T5, R> lambda) {
        return resolveSerializedLambda(lambda);
    }
    public static <T1, T2, T3, T4, R> SerializedLambda of(org.morecup.jimmerddd.java.function.base.FourParamHasReturn<T1, T2, T3, T4, R> lambda) {
        return resolveSerializedLambda(lambda);
    }
    public static <T1, T2, T3, T4, T5, T6, R> SerializedLambda of(org.morecup.jimmerddd.java.function.base.SixParamHasReturn<T1, T2, T3, T4, T5, T6, R> lambda) {
        return resolveSerializedLambda(lambda);
    }
    public static <T1, T2, T3, T4, T5, T6, T7, R> SerializedLambda of(org.morecup.jimmerddd.java.function.base.SevenParamHasReturn<T1, T2, T3, T4, T5, T6, T7, R> lambda) {
        return resolveSerializedLambda(lambda);
    }
    public static <T1, T2, T3, T4, T5, T6, T7, T8, R> SerializedLambda of(org.morecup.jimmerddd.java.function.base.EightParamHasReturn<T1, T2, T3, T4, T5, T6, T7, T8, R> lambda) {
        return resolveSerializedLambda(lambda);
    }
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> SerializedLambda of(org.morecup.jimmerddd.java.function.base.NineParamHasReturn<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> lambda) {
        return resolveSerializedLambda(lambda);
    }
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> SerializedLambda of(org.morecup.jimmerddd.java.function.base.TenParamHasReturn<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> lambda) {
        return resolveSerializedLambda(lambda);
    }
    public static <R> SerializedLambda of(org.morecup.jimmerddd.java.function.base.NoParamHasReturn<R> lambda) {
        return resolveSerializedLambda(lambda);
    }
    public static <T, R> SerializedLambda of(org.morecup.jimmerddd.java.function.base.OneParamHasReturn<T, R> lambda) {
        return resolveSerializedLambda(lambda);
    }
    public static <T1, T2, R> SerializedLambda of(org.morecup.jimmerddd.java.function.base.TwoParamHasReturn<T1, T2, R> lambda) {
        return resolveSerializedLambda(lambda);
    }
    public static <T1, T2, T3> SerializedLambda of(org.morecup.jimmerddd.java.function.base.ThreeParamNoReturn<T1, T2, T3> lambda) {
        return resolveSerializedLambda(lambda);
    }
    public static <T1, T2> SerializedLambda of(org.morecup.jimmerddd.java.function.base.TwoParamNoReturn<T1, T2> lambda) {
        return resolveSerializedLambda(lambda);
    }
    public static <T> SerializedLambda of(org.morecup.jimmerddd.java.function.base.OneParamNoReturn<T> lambda) {
        return resolveSerializedLambda(lambda);
    }
    public static SerializedLambda of(org.morecup.jimmerddd.java.function.base.NoParamNoReturn lambda) {
        return resolveSerializedLambda(lambda);
    }
    public static <T1, T2, T3, T4> SerializedLambda of(org.morecup.jimmerddd.java.function.base.FourParamNoReturn<T1, T2, T3, T4> lambda) {
        return resolveSerializedLambda(lambda);
    }
    public static <T1, T2, T3, T4, T5> SerializedLambda of(org.morecup.jimmerddd.java.function.base.FiveParamNoReturn<T1, T2, T3, T4, T5> lambda) {
        return resolveSerializedLambda(lambda);
    }
    public static <T1, T2, T3, T4, T5, T6> SerializedLambda of(org.morecup.jimmerddd.java.function.base.SixParamNoReturn<T1, T2, T3, T4, T5, T6> lambda) {
        return resolveSerializedLambda(lambda);
    }
    public static <T1, T2, T3, T4, T5, T6, T7> SerializedLambda of(org.morecup.jimmerddd.java.function.base.SevenParamNoReturn<T1, T2, T3, T4, T5, T6, T7> lambda) {
        return resolveSerializedLambda(lambda);
    }
    public static <T1, T2, T3, T4, T5, T6, T7, T8> SerializedLambda of(org.morecup.jimmerddd.java.function.base.EightParamNoReturn<T1, T2, T3, T4, T5, T6, T7, T8> lambda) {
        return resolveSerializedLambda(lambda);
    }
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> SerializedLambda of(org.morecup.jimmerddd.java.function.base.NineParamNoReturn<T1, T2, T3, T4, T5, T6, T7, T8, T9> lambda) {
        return resolveSerializedLambda(lambda);
    }
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> SerializedLambda of(org.morecup.jimmerddd.java.function.base.TenParamNoReturn<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> lambda) {
        return resolveSerializedLambda(lambda);
    }

    public static SerializedLambda resolveSerializedLambda(Serializable lambda) {
        return SerializableCache.computeIfAbsent(lambda, k -> {
            try {
                Method method = lambda.getClass().getDeclaredMethod("writeReplace");
                method.setAccessible(true);
                return (SerializedLambda) method.invoke(lambda);
            }catch (Exception e) {
                throw new JimmerDDDException("解析 Lambda 失败", e);
            }
        });
    }
}