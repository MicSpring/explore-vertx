package com.subha.java8.model;

import java.util.Iterator;

/**
 * Created by user on 1/12/2017.
 */
public class School<S,T> implements Iterable<T> {

    public interface MyMapper<X,Y>{
        Y applyMe(X x);
    }

    @Override
    public Iterator<T> iterator() {

        return new Iterator<T>() {
            T element = mapper.applyMe(student);
            @Override
            public boolean hasNext() {
                return element != null;
            }

            @Override
            public T next() {
               T elem = element;
                element = null;
                return elem;
            }
        };
    }

    String name;
    MyMapper<S,T> mapper;
    //T subject;
    S student;


   /* public School(String name, T subject) {
        this.name = name;
        this.subject = subject;
    }*/


    public String getName() {
        return name;
    }

    public School(String name , S student, MyMapper<S, T> mapper) {
        this.name = name;
        this.mapper = mapper;
        this.student = student;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MyMapper<S, T> getMapper() {
        return mapper;
    }

    public void setMapper(MyMapper<S, T> mapper) {
        this.mapper = mapper;
    }

    public S getStudent() {
        return student;
    }

    public void setStudent(S student) {
        this.student = student;
    }

    /* public T getSubject() {
        return subject;
    }

    public void setSubject(T subject) {
        this.subject = subject;
    }*/

    @Override
    public String toString() {
        return "School{" +
                "name='" + name + '\'' +
                ", mapper=" + mapper +
                ", student=" + student +
                '}';
    }
}
