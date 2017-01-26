package com.subha.java8.features;

import com.subha.java8.model.Student;
import com.subha.java8.model.Subject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by user on 1/15/2017.
 */
public class MyFunctionTest {

    static MyFunction<Student,String> function = s-> {Map<Student,String> m = new HashMap<Student,String>();
        m.put(s,s.getSubject().toString());
        return m.toString();};
    public static void main(String[] args) {
        System.out.println(new MyFunctionTest().testApply(s -> s.length()));

    }

    Integer testApply(MyFunction<String,Integer> myFunction){
        System.out.println(MyFunction.<Boolean>identity().apply(new AtomicBoolean().get()));
        System.out.println(myFunction.compose(e -> "100").apply(new Student("Subha",30,new Subject("Physics"))));
        System.out.println(myFunction.andThen(Integer::byteValue).apply("i AM gOOD "));
       return myFunction.apply("okies");
    }
}
