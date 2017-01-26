package com.subha.java8.features;

import com.subha.java8.model.Student;
import com.subha.java8.model.Subject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Created by user on 12/21/2016.
 */
public class MyHandlerTest<R> {

     R r;
     /*T t;*/

    MyHandlerTest(R r){
        this.r =r;
    }

    public static void main(String[] args) {
        System.out.println(getMyHandler(str-> getGreetings(str)));

        /*System.out.println*/

        //System.out.println(map(Student::new));
        System.out.println( new MyHandlerTest<Integer>(9).getMyGenericHandler(s -> s.length()));
    }


    private static String getMyHandler(MyHandler<? extends String,String> myHandler){
        return myHandler.myHandle("Mic");
    }
    private static String getGreetings(String str){
        return ("Hello! "+str+"  Welcome...");
    }
    private static Student map(BiFunction<String,Integer,Student> biFunc){
        return biFunc.apply("Subha",30);
    }

    private static Executor streamStudent(MyHandler<Subject,Student> func){
       return func::myHandle;
    }

    R  getMyGenericHandler(MyHandler<R,String> myHandler){
          return (myHandler.myHandle("Subha"));
    }

    Map<String, String> getMap(){
        Map<String,String> map = new HashMap<>();
        map.put("Subha","Paul");
        return map;
    }

}
