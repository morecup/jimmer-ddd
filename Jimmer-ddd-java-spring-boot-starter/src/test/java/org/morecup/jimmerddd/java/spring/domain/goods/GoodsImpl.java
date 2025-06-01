package org.morecup.jimmerddd.java.spring.domain.goods;

import org.morecup.jimmerddd.core.aggregateproxy.AggregateProxy;
import org.morecup.jimmerddd.core.aggregateproxy.EventHandler;

import static org.morecup.jimmerddd.core.aggregateproxy.GlobalContext.nullDraftContext;

public interface GoodsImpl extends GoodsDraft, EventHandler {
    AggregateProxy<GoodsImpl> proxy = new AggregateProxy<>(GoodsImpl.class);

    default void test() {
        String name = name();
        nullDraftContext(()->{
            return name;
        });
        System.out.println("test");
        System.out.println(name());
        setNowAddress("新地址");
        System.out.println(nowAddress());
//        return "testResult";
    }
}
