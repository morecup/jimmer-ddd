package org.morecup.jimmerddd.java.spring.domain.goods;

import org.morecup.jimmerddd.core.aggregateproxy.AggregateProxy;
import org.morecup.jimmerddd.core.aggregateproxy.EventHandler;

public interface GoodsImpl extends GoodsDraft, EventHandler {
    AggregateProxy<GoodsImpl> proxy = new AggregateProxy<>(GoodsImpl.class);

    default void test() {
        System.out.println("test");
        System.out.println(name());
        setNowAddress("新地址");
        System.out.println(nowAddress());
//        return "testResult";
    }
}
