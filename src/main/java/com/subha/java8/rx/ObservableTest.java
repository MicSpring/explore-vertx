package com.subha.java8.rx;

import com.subha.java8.model.School;
import com.subha.java8.model.Student;
import com.subha.java8.model.Subject;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * Created by user on 12/28/2016.
 */
public class ObservableTest {
    public static void main(String[] args) throws InterruptedException {
        createNameObservable().subscribe(
                str->{
                    System.out.println("The Data is:"+str);
                },
                t->{
                    t.printStackTrace();
                },
                ()->{
                    System.out.println("Completed");
                }

        );

        List<String> list = new ArrayList<>();
        list.add("Subha");
        list.add("Mic");
        createNameObservable().all(str->{
            System.out.println("The String is:"+str);
            return str.length() >7;
        });

        IntStream.range(30,40).forEach(value -> {
            System.out.print("@@@");
        });

        System.out.println("");
        Observable.just("1","2","3","4","5","6").buffer(2).subscribe(
                strings -> System.out.println(strings),
                ObservableTest::printError,
                () -> System.out.println("Buffer Done....")

        );

        IntStream.range(30,40).forEach(value -> {
            System.out.print(" *** ");
        });

        System.out.println("");
        getIt(()-> new School<Student,Subject>("Margaret",
                new Student("Subha",30,new Subject("Maths")),
                student -> student.getSubject()))
                .subscribe(subject -> System.out.println(subject),
                           Throwable::printStackTrace,
                            () -> System.out.println("JUST FINEEEE....")
                );


    }

    private static Observable<String> createNameObservable(){
           return Observable.<List<String>>create(subscriber->{
                List<String> langList = new ArrayList<String>();
                langList.add("Java");
               langList.add("Groovy");
               subscriber.onNext(langList);

               List<String> ideList = new ArrayList<String>();
               ideList.add("intelliJ");
               ideList.add("Eclipse");

               subscriber.onNext(ideList);

            }).doOnCompleted(()->{
               System.out.println("Do On Completed.....");
           }).
                   flatMap(list->{
               return Observable.from(list);
           }).doOnNext(str->{
               System.out.println("**** Do On Next:"+str);}).doOnNext(str->{
               System.out.println("**** Do On Next Another:"+str);}).doOnCompleted(()->{
               System.out.println(" Again Do On Completed.....");
           });

    }

    private static void printError(Throwable e){
        e.printStackTrace();
    }

  static public  <S,T> Observable<T> getIt(Supplier<School<S,T>> schoolSupplier){
        return Observable.using(() -> schoolSupplier.get(),
                                    school -> Observable.from(school),
                                    resource -> System.out.println("*** Observable Creation Completed....")

        );
    }
}
