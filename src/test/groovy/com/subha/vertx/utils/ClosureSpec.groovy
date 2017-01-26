package com.subha.vertx.utils

import spock.lang.Specification

/**
 * Created by user on 1/22/2017.
 */
class ClosureSpec extends Specification {
    def "test closure"(){

        when:
            def retVal = function([1,"Mic"],{str1,str2 -> str1.size()+" Paul "+ Arrays.asList(str2)})
            def cls = {str ->  println "My New Nameee is: $str"}
            def line = {str ->  println "My New LINE is: $str"}
            def names = ['Subha','Mic',100]
            def players = ['Sachin','Sehwag','Laxman']

            readFile(new File("C:\\Users\\user\\Desktop\\Vertx.txt"),/*this.&transform*/line)
        then:
            names.each (cls)
            names.each {this.&transform(it)}
            players.each (this.&transform)
            println "The RetVal is: ${retVal('Mickey')}"
            retVal('Mic') == "2 Paul [Mic]"
    }

    def function(str,cl){
        return { name->
            cl(str,name)
        }
    }

    def transform(str){
        println "My Name is: $str"
    }

    def readFile(File file,c1){
        def reader = file.newReader()
        reader.splitEachLine('\n'){line->c1(line)}
        reader.close()
    }
}
