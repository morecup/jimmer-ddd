package org.morecup.jimmerddd.java.factory;

import java.util.ArrayList;
import java.util.List;

public class JFactoryContext {
    public final List<Object> lazyEventList = new ArrayList<>();

    public void addLazyEvent(Object event) {
        lazyEventList.add(event);
    }
}
