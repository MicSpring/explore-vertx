package com.subha.vertx.verticles

import com.google.inject.Inject
import com.subha.vertx.guice.dependency.Dependency
import com.subha.vertx.handler.DataHandler
import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpClientResponse
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.ext.web.handler.sockjs.SockJSHandler
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions
import io.vertx.servicediscovery.ServiceDiscovery
/**
 * Created by user on 12/14/2016.
 */
class Server3 extends AbstractVerticle{

    Dependency dependency
    ServiceDiscovery serviceDiscovery

    @Inject
    public Server3(Dependency dependency){
        this.dependency = dependency
    }

    public void start() throws Exception {

        serviceDiscovery = ServiceDiscovery.create(vertx)
        println " **** The Dependency is: ${dependency.serve()}"


        def sockJSHandlerOptions = new SockJSHandlerOptions().setHeartbeatInterval(3000)

        def sockJSHandler = SockJSHandler.create(vertx,sockJSHandlerOptions)
        sockJSHandler.socketHandler(new DataHandler())

        def router = Router.router(vertx)

        router.route(HttpMethod.GET,'/test').handler(sockJSHandler)
        router.route(HttpMethod.GET,'/getB').handler({routingContext->
                invokeB(routingContext,serviceDiscovery);})

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

    private void invokeB(RoutingContext context, ServiceDiscovery serviceDiscovery){
        serviceDiscovery.getRecord(new JsonObject().put("name", "Server2"), { ar ->
            if (ar.succeeded() && ar.result() != null) {
                // Retrieve the service reference
                def reference = serviceDiscovery.getReference(ar.result())
                // Retrieve the service object
                def client = reference.get()
                println "The Client class is: $client"
                // You need to path the complete path
                client.getNow("/", { HttpClientResponse response ->

                    // ...
                    //println "The Response is: ${response.getBody}"
                    response.handler({buffer -> buffer.toString()})
                    // Dont' forget to release the service
                    reference.release()

                })
            }
            else{
                println("Alas!!! Server2 Not Found.....")
            }
        })
    }
}
