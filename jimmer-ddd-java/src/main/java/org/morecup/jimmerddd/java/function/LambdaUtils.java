package org.morecup.jimmerddd.java.function;

import org.morecup.jimmerddd.java.function.base.ThreeParamHasReturn;

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class LambdaUtils {
    // 缓存 SerializedLambda 解析结果（提升性能）
    private static final Map<Class<?>, Map<String, String>> CACHE = new ConcurrentHashMap<>();

    /**
     * 解析方法引用，返回数据库字段名（如 user_name）
     */
//    public static <T> String getColumnName(OneParamHasReturn<T, ?> lambda) {
//        try {
//            SerializedLambda serializedLambda = resolveSerializedLambda(lambda);
//            // 从缓存获取字段名
//            return getFromCache(serializedLambda, () -> {
//                String methodName = serializedLambda.getImplMethodName();
//                String propertyName = methodToProperty(methodName);
//                return camelToUnderline(propertyName);
//            });
//        } catch (Exception e) {
//            throw new RuntimeException("解析 Lambda 失败", e);
//        }
//    }

    public <T, U, V, R> String getColumnName(ThreeParamHasReturn<T, U, V, R> lambda) {
        try {
            SerializedLambda serializedLambda = resolveSerializedLambda(lambda);
            // 从缓存获取字段名
            return getFromCache(serializedLambda, () -> {
                String methodName = serializedLambda.getImplMethodName();
                String propertyName = methodToProperty(methodName);
                return camelToUnderline(propertyName);
            });
        } catch (Exception e) {
            throw new RuntimeException("解析 Lambda 失败", e);
        }
    }

    /**
     * 反射获取 SerializedLambda
     */
    private static SerializedLambda resolveSerializedLambda(Serializable lambda) throws Exception {
        Method method = lambda.getClass().getDeclaredMethod("writeReplace");
        method.setAccessible(true);
        return (SerializedLambda) method.invoke(lambda);
    }

    /**
     * 方法名 → 属性名（如 getName → name）
     */
    private static String methodToProperty(String methodName) {
        if (methodName.startsWith("is")) {
            return Introspector.decapitalize(methodName.substring(2));
        } else if (methodName.startsWith("get") || methodName.startsWith("set")) {
            return Introspector.decapitalize(methodName.substring(3));
        }
        throw new IllegalArgumentException("无效的 Getter 方法: " + methodName);
    }

    /**
     * 驼峰转下划线（如 userName → user_name）
     */
    private static String camelToUnderline(String str) {
        return str.replaceAll("([A-Z])", "_$1").toLowerCase();
    }

    /**
     * 缓存逻辑
     */
    private static String getFromCache(SerializedLambda lambda, Supplier<String> resolver) {
        return CACHE.computeIfAbsent(lambda.getClass(), k -> new ConcurrentHashMap<>())
                .computeIfAbsent(lambda.getImplMethodName(), k -> resolver.get());
    }
}