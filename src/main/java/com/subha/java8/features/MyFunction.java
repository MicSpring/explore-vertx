package com.subha.java8.features;

import java.util.function.Function;

/**
 * Created by user on 1/15/2017.
 */
@FunctionalInterface
public interface MyFunction<T,R> {

    R apply(T t);

    /**
     *
     * @param <T>
     * @return
     *
     * A Functional Interface methods can have a body in 2 Cases
     * 1) If declared default
     * 2) If the method is static
     */

     static <T>  MyFunction<T,T> identity(){
        return m -> {
            System.out.println("The Class is:"+m.getClass());
            return m;};
    }

    default <V> MyFunction<V,R> compose(MyFunction<? super V, T> myFunction){
            return v -> apply(myFunction.apply(v));
    }
    default <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {
        return (T t) -> after.apply(apply(t));
    }

}
