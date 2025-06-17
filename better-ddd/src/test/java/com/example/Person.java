package com.example;

import org.morecup.jimmerddd.betterddd.annotation.AggregateRoot;

@AggregateRoot
public class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void updateName(String newName) {
        this.name = newName;
    }

    public String getName() {
        return this.name;
    }
}