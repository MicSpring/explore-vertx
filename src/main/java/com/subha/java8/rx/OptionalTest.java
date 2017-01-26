package com.subha.java8.rx;

import com.subha.java8.model.Student;

import java.util.Optional;

/**
 * Created by user on 1/11/2017.
 */
public class OptionalTest {

    public static void main(String[] args) {
        Student student = null;/* new Student();
        student.setSubject(new Subject(""));
*/
        String ok = Optional.ofNullable(student)
                .map(stud-> stud.getSubject())
                .map(subject -> subject.getName())
                .orElse("Name no present");

        System.out.println(ok);

    }
}
