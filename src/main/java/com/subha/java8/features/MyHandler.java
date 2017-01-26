package com.subha.java8.features;

/**
 * Created by user on 12/21/2016.
 */
@FunctionalInterface
public interface MyHandler<R,T> {
    public R myHandle(T t);
}
