package com.subha.vertx.handler

import io.vertx.core.Handler
import io.vertx.groovy.ext.web.handler.sockjs.SockJSSocket

/**
 * Created by user on 12/17/2016.
 */
class DataHandler implements Handler<SockJSSocket>{
    @Override
    void handle(SockJSSocket event) {
        println "**** The Handles Object is: $event"
        event.handler({buffer->
            println "The  Data is: ${buffer.toString()}"
        })
    }
}
