package com.subha.vertx.handler

import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.RoutingContext

/**
 * Created by user on 12/18/2016.
 */
class EventBusHandler /*implements Handler*/{

    /*@Override
    void handle(Object event) {
        def buff = Buffer.buffer().appendString("{\"price\":\"5678\"}")
        event.setBody(buff)
        event.vertx().eventBus().publish("auction" , event.getBodyAsString());
        event.response().setStatusCode(200).end()
    }*/

    public void generateSockData(RoutingContext context){
        def buff = Buffer.buffer().appendString("{\"price\":\"9999\"}")
        context.setBody(buff)
        context.vertx().eventBus().publish("auction" , context.getBodyAsString());
        context.response().setStatusCode(200).end()
    }
}
