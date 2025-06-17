package com.example;

public class TestMain {
    public static void main(String[] args) {
        Person p = new Person("Tom", 30);
        System.out.println(p.getName());
        p.updateName("Jerry");
//        System.out.println(p.name);
//        p.age = 35;
//        System.out.println(p.age);
    }
}