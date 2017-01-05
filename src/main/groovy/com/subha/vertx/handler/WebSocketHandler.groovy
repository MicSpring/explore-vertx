package com.subha.vertx.handler

import io.vertx.core.Handler
import io.vertx.core.http.ServerWebSocket
/**
 * Created by user on 12/29/2016.
 */
class WebSocketHandler implements Handler<ServerWebSocket>{

    @Override
    void handle(ServerWebSocket serverWebSocket) {

        println "***** Path: ${serverWebSocket.path()}"
        println "***** URI: ${serverWebSocket.uri()}"
        println "***** LocalAddress: ${serverWebSocket.localAddress()}"

        serverWebSocket.setWriteQueueMaxSize(Integer.MAX_VALUE);
        serverWebSocket//.handler({buffer-> println "The Websocket Data at the Server is: ${buffer.toString()}"})
            .exceptionHandler({ex->ex.printStackTrace()})
            .frameHandler(new WebSocketFrameHandler({buf->serverWebSocket.writeBinaryMessage(buf)}))
            //.writeBinaryMessage(Buffer.buffer("Sending Data to the Client Again....".getBytes()))
    }
}
