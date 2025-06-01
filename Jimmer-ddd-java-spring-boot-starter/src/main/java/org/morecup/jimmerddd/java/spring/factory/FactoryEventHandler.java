package org.morecup.jimmerddd.java.spring.factory;

import org.jetbrains.annotations.NotNull;
import org.morecup.jimmerddd.core.event.EventHandler;
import org.morecup.jimmerddd.core.event.EventManager;

import static org.morecup.jimmerddd.core.aggregateproxy.GlobalContext.nullDraftContext;

public interface FactoryEventHandler extends EventHandler {
    @Override
    default void publishEvent(@NotNull Object event){
        nullDraftContext(()->{
            EventManager.publish(event);
            return null;
        });
    }

    @Override
    default void lazyPublishEvent(@NotNull Object event){
        JFactoryContext context = FactoryContextManager.getContext();
        if (context != null){
            context.addLazyEvent(event);
        }
    }
}
