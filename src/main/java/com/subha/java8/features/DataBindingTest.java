package com.subha.java8.features;


import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.subha.java8.model.Book;
import org.codehaus.stax2.XMLOutputFactory2;
import org.codehaus.stax2.XMLStreamWriter2;

import javax.xml.stream.XMLStreamException;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by user on 1/16/2017.
 */
public class DataBindingTest {
    public static void main(String[] args) throws IOException, XMLStreamException {

        Book book = new Book();
        book.setName("Groovy");
        book.setAuthor("Gordon");

        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.writeValue(((        XMLStreamWriter2
                )XMLOutputFactory2.newFactory().createXMLStreamWriter
                (new FileWriter("C:\\Users\\user\\Downloads\\BooksOut2.xml"))), book);
    }
}
