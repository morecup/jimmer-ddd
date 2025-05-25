package org.morecup.jimmerddd.java;

import org.morecup.jimmerddd.core.preanalysis.MethodInfo;
import org.morecup.jimmerddd.java.function.MI;

public class Main {
    public static void main(String[] args) {
        new Main().test();
        new Main().test();
    }

    private void test() {
        MethodInfo mi = MI.of(TestInt::test);
        System.out.println(mi);
    }
}
