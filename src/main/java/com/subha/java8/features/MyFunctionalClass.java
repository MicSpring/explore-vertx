package com.subha.java8.features;

import java.util.function.Function;

/**
 * Created by user on 1/27/2017.
 */
public class MyFunctionalClass {
    private String name;
    private String age;

    static Function<String, Integer> demoFunc;

    public MyFunctionalClass(String name, String age) {
        this.name = name;
        this.age = age;
    }

    public static Integer testme(String str){
        return str.length();
    }

    public static void testMyFunctionalInterface(MyFunctionalInterface myFunctionalInterface){
    }

    public static void main(String[] args) {
        testMyFunctionalInterface(demoFunc::apply);
    }

}
