package com.subha.vertx.verticles

import com.google.inject.Inject
import com.subha.vertx.guice.dependency.Dependency
import com.subha.vertx.handler.DataHandler
import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.ext.web.handler.sockjs.SockJSHandler
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions


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

        def sockJSHandlerOptions = new SockJSHandlerOptions().setHeartbeatInterval(3000)

        def sockJSHandler = SockJSHandler.create(vertx,sockJSHandlerOptions)
        sockJSHandler.socketHandler(new DataHandler())

        def router = Router.router(vertx)

        router.route(HttpMethod.GET,'/test').handler(sockJSHandler)

        router.route('/*').handler(
                StaticHandler.create())

        vertx.createHttpServer(new HttpServerOptions()
                .setPort(config().getInteger("http.port.server3",8093))
                .setHost("localhost"))
                .requestHandler(router.&accept).listen({
            httpServer->
                if(!httpServer.succeeded())
                    httpServer.cause().printStackTrace()
                else
                    println "Server 3 StrartUp Completed...."
        })
    }
}
