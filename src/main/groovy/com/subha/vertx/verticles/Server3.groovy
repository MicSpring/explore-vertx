package com.subha.vertx.verticles

import com.google.inject.Inject
import com.subha.vertx.guice.dependency.Dependency
import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpServerOptions

/**
 * Created by user on 12/14/2016.
 */
class Server3 extends AbstractVerticle{

    Dependency dependency

    @Inject
    public Server3(Dependency dependency){
        this.dependency = dependency
    }

    public void start() throws Exception {

        println " **** The Dependency is: ${dependency.serve()}"

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
