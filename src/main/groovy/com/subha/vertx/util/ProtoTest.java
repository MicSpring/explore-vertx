package com.subha.vertx.util;

import com.subha.vertx.proto.AddressBookProtos;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by user on 1/20/2017.
 */
public class ProtoTest {
    public static void main(String[] args) throws IOException {
        AddressBookProtos.Person john =
                AddressBookProtos.Person.newBuilder()
                        .setId(1234)
                        .setName("John Doe")
                        .setEmail("jdoe@example.com")
                        .addPhones(AddressBookProtos.Person.PhoneNumber.newBuilder()
                                        .setNumber("555-4321")
                                        .setType(AddressBookProtos.Person.PhoneType.HOME))
                        .build();

        System.out.println("Before Serialization:"+john);

        john.writeTo(new FileOutputStream("E:\\My_Work\\explore-vertx\\proto.ser"));

        System.out.println("After Serialization:"+AddressBookProtos.Person.parseFrom(new FileInputStream("E:\\My_Work\\explore-vertx\\proto.ser")));

    }
}
