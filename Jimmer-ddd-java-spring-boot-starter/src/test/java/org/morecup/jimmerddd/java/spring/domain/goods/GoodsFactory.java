package org.morecup.jimmerddd.java.spring.domain.goods;

import org.morecup.jimmerddd.java.spring.factory.FactoryEventHandler;
import org.morecup.jimmerddd.java.spring.factory.WithFactoryContext;
import org.springframework.stereotype.Component;

@Component
public class GoodsFactory implements FactoryEventHandler {
    @WithFactoryContext
    public Goods createAndSave() {
        return Immutables.createGoods(draft -> {
            draft.setName("Goods");
            draft.setNowAddress("address");
        });
    }
}
