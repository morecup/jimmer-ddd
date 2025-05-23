package org.morecup.jimmerddd.java;

import org.morecup.jimmerddd.java.function.LambdaUtils;

public class Main {
    public static void main(String[] args) {
        new Main().test();

    }

    private void test() {
        new LambdaUtils().getColumnName(TestInt::test);
    }
}
