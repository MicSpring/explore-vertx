package com.subha.java8.features;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by user on 12/20/2016.
 */
public class IntStreamTest {

    public static void main(String[] args){

        IntStream intStream = IntStream.range(1,10).flatMap(val->{
            System.out.println("The Value is:"+val);
            return IntStream.range(1,val*2);
        });
        intStream.boxed().forEach(val -> {
            System.out.println("**The Value is:"+val);
        });

        Map<String,Double> resultMap = new HashMap<>();
        resultMap.put("USB:2016-12-01",12d);
        resultMap.put("USU:2016-12-11",123d);
        resultMap.put("OSU:2016-12-13",125d);
        resultMap.put("OSB:2011-12-14",102d);
        resultMap.put("OSB:2016-11-14",103d);

       resultMap.entrySet().stream().filter(entry ->{
            return entry.getKey().contains("USB") || entry.getKey().contains("OSU");
        }).flatMap(entry ->{
            return Arrays.stream(new Double[]{entry.getValue()});
        }).forEach(System.out::println);




        System.out.println(

       resultMap.entrySet().stream().filter(entry ->{
            return entry.getKey().contains("USU") || entry.getKey().contains("OSB");
        }).collect(Collectors.groupingBy(val -> {
          return val.getKey().substring(3);
      })).entrySet().stream().collect(Collectors.toMap(entry1->{
           return entry1.getKey();}
               ,
               entry2->{
                   List<Map.Entry<String,Double>> outList = entry2.getValue();
                   Map outMap = outList.stream().collect(Collectors.toMap(keyMapper->{
                       return keyMapper.getKey().split("\\:")[0];
                   },
                           valueMapper->{
                               return valueMapper.getValue();
                           })
                   )/*.collect(Collectors.toList())*/;
                   return outMap;
       }))

        );

    }

    private static Integer func(Function<String, Integer> f1){
       return  f1.apply("ok");
    }

}
