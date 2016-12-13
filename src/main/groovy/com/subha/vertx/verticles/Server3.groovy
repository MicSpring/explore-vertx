package com.subha.vertx.verticles

import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpServerOptions

/**
 * Created by user on 12/14/2016.
 */
class Server3 extends AbstractVerticle{

    public void start() throws Exception {

        vertx.createHttpServer(new HttpServerOptions()
                .setPort(config().getInteger("http.port.server3",8093))
                .setHost("localhost"))
                .requestHandler({
            httpServerRequest->
                httpServerRequest.response().setChunked(true).write(" Hi! Welcome to Vertx Server3....")
                .end()
        }).listen({
            httpServer->
                if(!httpServer.succeeded())
                    httpServer.cause().printStackTrace()
                else
                    println "Server 3 StrartUp Completed...."
        })
    }
}
