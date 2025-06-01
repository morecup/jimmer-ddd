package org.morecup.jimmerddd.java.spring.aggregateproxy;

import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.junit.jupiter.api.Test;
import org.morecup.jimmerddd.java.preanalysis.FunctionFetcher;
import org.morecup.jimmerddd.java.spring.Application;
import org.morecup.jimmerddd.java.spring.domain.goods.Goods;
import org.morecup.jimmerddd.java.spring.domain.goods.GoodsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Application.class)
public class AggregateProxyTest {
    @Autowired
    private JSqlClient sql;

    private static final Long testGoodsId = 1928974921409630208L;

    @Test
    void test() {
        // 输出当前Java版本
        System.out.println("当前Java版本: " + System.getProperty("java.version"));
        try {
            Fetcher<Goods> goodsFetcher = FunctionFetcher.of(Goods.class, GoodsImpl::test);
            Goods goods = sql.findById(goodsFetcher, testGoodsId);
            String result = GoodsImpl.proxy.execAndSave(goods, impl -> {
                impl.test();
                return null;
            });
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}