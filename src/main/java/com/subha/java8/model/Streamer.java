package com.subha.java8.model;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by user on 12/29/2016.
 */
public class Streamer {
    private Collection<String> streamer;

    private Streamer(String... str){
        streamer = Stream.of(str).collect(Collectors.toList());
    }

    public static Streamer of(String... str){
        return new Streamer(str);
    }
}
