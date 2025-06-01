package org.morecup.jimmerddd.java.spring.factory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithFactoryContext {
    boolean autoSave() default true;
    boolean autoPublishLazyEventAfterSave() default true;
}
