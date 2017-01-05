package com.subha.vertx.handler

import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.WebSocketFrame

import java.util.function.Consumer

/**
 * Created by user on 1/5/2017.
 */
class WebSocketFrameHandler implements Handler<WebSocketFrame>{

    Consumer<Buffer> consumerbuffer;
    public WebSocketFrameHandler( Consumer<Buffer> consumerbuffer){
        this.consumerbuffer = consumerbuffer
    }

    @Override
    void handle(WebSocketFrame frame) {
        println "The Data at the Server Socket is:${frame.textData()}"
        consumerbuffer.accept(frame.binaryData())
    }
}
