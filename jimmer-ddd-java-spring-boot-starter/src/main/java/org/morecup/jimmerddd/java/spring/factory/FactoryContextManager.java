package org.morecup.jimmerddd.java.spring.factory;

import java.util.Stack;

public class FactoryContextManager {
    public static final ThreadLocal<JFactoryContext> contextHolder = new ThreadLocal<>();
    public static final ThreadLocal<Stack<JFactoryContext>> contextBefore = ThreadLocal.withInitial(Stack::new);

    public static JFactoryContext getContext() {
        return contextHolder.get();
    }

    public static void setContext(JFactoryContext context) {
        JFactoryContext nowContext = getContext();
        if (nowContext != null){
            contextBefore.get().push(nowContext);
        }
        contextHolder.set(context);
    }

    public static void clearContext() {
        contextHolder.remove();
        Stack<JFactoryContext> jFactoryContexts = contextBefore.get();
        if (!jFactoryContexts.isEmpty()){
            contextHolder.set(jFactoryContexts.pop());
        }
    }

    /**
     * 清除当前线程的整个上下文栈
     */
    public static void clearAllContexts() {
        contextHolder.remove();
        contextBefore.remove();
    }
}
