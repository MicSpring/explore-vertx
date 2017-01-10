package com.subha.java8.model;

/**
 * Created by user on 12/29/2016.
 */
public class Student {
    String name;
    Integer age;

    public Student(String name,Integer age){
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
