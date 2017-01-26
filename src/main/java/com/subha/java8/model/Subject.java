package com.subha.java8.model;

/**
 * Created by user on 1/5/2017.
 */
public class Subject {
    String name;

    public Subject(){
    }

    public Subject(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "name='" + name + '\'' +
                '}';
    }
}
