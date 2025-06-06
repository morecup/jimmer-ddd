package org.morecup.jimmerddd.java.spring.factory;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.morecup.jimmerddd.core.JimmerDDDConfig;
import org.morecup.jimmerddd.core.event.EventManager;
import org.morecup.jimmerddd.java.factory.FactoryContextManager;
import org.morecup.jimmerddd.java.factory.JFactoryContext;
import org.morecup.jimmerddd.java.factory.WithFactoryContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static org.morecup.jimmerddd.core.aggregateproxy.GlobalContext.nullDraftContextWithThrowable;

@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 1) // 确保在用户自定义切面之前执行
public class JFactoryAop {
    private static final Logger logger = LoggerFactory.getLogger(JFactoryAop.class);

    // 定义切入点，拦截含有 WithFactoryContext 注解的方法
    @Pointcut("@annotation(org.morecup.jimmerddd.java.factory.WithFactoryContext)")
    public void withFactoryContextPointcut() {}

    @Around("withFactoryContextPointcut()")
    public Object afterAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取WithFactoryContext注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        WithFactoryContext withFactoryContext = method.getAnnotation(WithFactoryContext.class);
        return nullDraftContextWithThrowable(() -> {
            FactoryContextManager.setContext(new JFactoryContext());
            try {
                Object result =  joinPoint.proceed();
                if (withFactoryContext.autoSave()){
                    Object modifiedEntity = JimmerDDDConfig.INSTANCE.getSaveEntityFunction().invoke(result);
                    if (withFactoryContext.autoPublishLazyEventAfterSave()){
                        FactoryContextManager.getContext().lazyEventList.forEach(EventManager::publish);
                    }
                    return modifiedEntity;
                }
                return result;
            } finally {
                FactoryContextManager.clearContext();
            }
        });
    }

}