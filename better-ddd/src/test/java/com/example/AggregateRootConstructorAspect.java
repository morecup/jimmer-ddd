package com.example;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Arrays;

@Aspect
public class AggregateRootConstructorAspect {

    @Pointcut("execution(*.new(..)) && @within(org.morecup.jimmerddd.betterddd.annotation.AggregateRoot)")
    public void constructorOfAggregateRoot() {}

    @Before("constructorOfAggregateRoot()")
    public void beforeConstructor(JoinPoint joinPoint) {
        System.out.println("即将构造 AggregateRoot 对象：" + joinPoint.getSignature());
    }

    // 如果想用 @Around 拦截，注意构造函数无返回值，不能替换实例
    @Around("constructorOfAggregateRoot()")
    public Object aroundConstructor(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("环绕通知：构造 AggregateRoot 对象，参数：" + Arrays.toString(pjp.getArgs()));

        Object obj = pjp.proceed();

        System.out.println("构造完成：" + obj); // 这里 obj 是 null，因为构造函数无返回值

        return obj;
    }

//    @Pointcut("call(*.new(..)) && @within(org.morecup.jimmerddd.betterddd.annotation.AggregateRoot)")
//    public void callConstructorOfAggregateRoot() {}
}
