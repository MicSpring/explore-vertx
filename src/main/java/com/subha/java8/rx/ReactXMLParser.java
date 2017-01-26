package com.subha.java8.rx;

import com.subha.java8.model.Student;
import javaslang.API;
import javaslang.Tuple;
import javaslang.collection.HashMap;
import javaslang.collection.Map;
import rx.Observable;

import javax.xml.stream.*;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

/**
 * Created by user on 1/11/2017.
 */
public class ReactXMLParser {

    static Map<String,BiConsumer<XMLStreamReader,Student>> mappers = HashMap.ofEntries(
            Tuple.of("bookAttr",ReactXMLParser::processForAttributes),
            Tuple.of("bookElem",ReactXMLParser::processForElement)
    );

    static String str = null;
    public  Observable<XMLEvent> toXMLObservable(Path path){
        return Observable.using(() -> xmlEventReaderResource(path),
                this::toXMLEventObservable,
                this::closeReader
        );
    }

    public  Observable<String> toStringObservable(){
        return Observable.using(this::stringResource,
                this::toStringObservable,
                strResource -> System.out.println(Arrays.asList(strResource))
        );
    }

    private  String[] stringResource(){
        return new String[]{"Mic", "Puchu", "Subha"};
    }

    private  Observable<String> toStringObservable(String[] strArr){
        return Observable.from(strArr);
    }

    public static void main(String[] args) throws IOException, XMLStreamException {
        new ReactXMLParser().toStringObservable().subscribe(
                data-> System.out.println("The Data being:"+data),
                ex -> Observable.error(ex),
                () -> System.out.println("Completed")
        );

        IntStream.range(10,20).forEach(i-> System.out.print("*************"));
        System.out.println();

        new ReactXMLParser().toXMLObservable(Paths.get("C:\\Users\\user\\Downloads\\books.xml"))
                .subscribe(xmlEvent -> {
                    try {
                        API.Match(xmlEvent.getEventType()).of(

                                API.Case(API.$(1), o -> API.run(() -> {
                                    System.out.println(xmlEvent.asStartElement().getName().getLocalPart());
                                    Optional.of(xmlEvent.asStartElement())
                                            .map(startElement -> startElement.getAttributes())
                                            .map(iterator ->
                                                 Observable.create(subscriber -> {
                                                     while (iterator.hasNext()) {
                                                         Attribute attr = (Attribute) iterator.next();
                                                         subscriber.onNext(attr.getName().getLocalPart() + ":" + attr.getValue());
                                                     }
                                                 }
                                                )
                                            ).orElse(Observable.just("No Attributes"))
                                            .subscribe(
                                            attr-> System.out.println(attr),
                                            attrEx -> attrEx.printStackTrace(),
                                            () -> System.out.println("")
                                    );


                                })),
                                API.Case(API.$(2), o -> API.run(() -> {
                                    System.out.println(xmlEvent.asEndElement().getName().getLocalPart());
                                })),
                                API.Case(API.$(4), o -> API.run(() -> {
                                    System.out.println(xmlEvent.asCharacters().toString());
                                })),
                                API.Case(API.$(), o -> API.run(() -> {
                                    System.out.println("Bypassing...");
                                }))

                        );
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                    }
                },
                 ex-> Observable.error(ex),
                        ()-> System.out.println("Completed...")

                );

        toXMLWriteObservable();

        System.out.println(":::::::::::::::::::::::::::::::::");
        System.out.println("::::::::::::::::::::::::::::::::::::::::");
        processXMLReactively(Paths.get("C:\\Users\\user\\Downloads\\books_new.xml"));


    }

    private  XMLEventReader xmlEventReaderResource(Path path){
        XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
        XMLEventReader xmlEventReader = null;
        try {
            xmlEventReader = xmlInputFactory.createXMLEventReader(new FileReader(path.toFile()));
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return xmlEventReader;
    }

    private  Observable<XMLEvent> toXMLEventObservable(XMLEventReader xmlEventReader){
       return Observable.<XMLEvent>create(subscriber -> {
                    while (xmlEventReader.hasNext()) {
                        try {
                            subscriber.onNext(xmlEventReader.nextEvent());
                        } catch (XMLStreamException e) {
                            e.printStackTrace();
                            subscriber.onError(e);
                        }
                    }
                    subscriber.onCompleted();
                }
        );

    }

    private void closeReader(XMLEventReader xmlEventReader){
        try {
            xmlEventReader.close();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    /**
     * Under Construction
     */

    static private void toXMLWriteObservable() throws IOException, XMLStreamException {
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();
        XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(new FileWriter("C:\\Users\\user\\Downloads\\BooksOut.xml"));
        xmlStreamWriter.writeStartDocument("UTF-8","1.0");
        xmlStreamWriter.setPrefix("zip", "com.test.zip");
        xmlStreamWriter.writeStartElement("com.test.zip", "archive");

        xmlStreamWriter.writeNamespace("zip","com.test.zip");
        xmlStreamWriter.writeNamespace("zip2","com.test.zip2");

        xmlStreamWriter.setPrefix("zip2", "com.test.zip4");
        xmlStreamWriter.writeStartElement("com.test.zip4","element");
        xmlStreamWriter.writeNamespace("zip3","com.test.zip3");
        xmlStreamWriter.writeEndElement();

        xmlStreamWriter.writeEndElement();
        xmlStreamWriter.writeEndDocument();


        xmlStreamWriter.flush();
        xmlStreamWriter.close();

    }

    private static void processXMLReactively(Path path){

        try {
            XMLStreamReader xmlStreamReader = XMLInputFactory.newFactory().createXMLStreamReader(new FileReader(path.toFile()));
            while(xmlStreamReader.hasNext()){
                if(xmlStreamReader.next() == XMLStreamReader.START_ELEMENT && xmlStreamReader.getName().getLocalPart().equals("book")){
                    processForAttributes(xmlStreamReader,new Student());
                    processForElement(xmlStreamReader,new Student());
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private static void processForAttributes(XMLStreamReader reader,Student student){
        //try {
          // if(reader.hasNext()){
               IntStream.range(0,reader.getAttributeCount())
                       .forEach(value -> {
                           if("ok".equals(reader.getAttributeLocalName(value)))
                                System.out.println(
                                   reader.getAttributeName(value)+"---"+reader.getAttributeValue(value));});

          /* }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }*/
    }

    private static void processForElement(XMLStreamReader reader,Student student){
        try {
            while(reader.hasNext()){

             switch (reader.next()){
                   case XMLStreamReader.START_ELEMENT:
                   {
                       System.out.println("Start Element:"+reader.getName().getLocalPart());
                       break;
                   }
                   case XMLStreamReader.CHARACTERS:
                   {
                       System.out.println("Data:"+(reader.getText()));
                       break;
                   }
                   case XMLStreamReader.END_ELEMENT:
                   {
                       if("book".equals(reader.getName().getLocalPart()))
                           return;
                       else {
                           System.out.println("End Element:" + reader.getName().getLocalPart());
                           break;
                       }
                   }
                   default:
                       break;

               }

                System.out.println("************");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
