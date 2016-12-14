package com.subha.vertx.verticles

import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.Module
import com.subha.vertx.binder.ServiceBinder
import com.subha.vertx.guice.launcher.GuiceVertxLauncher
import com.subha.vertx.guice.module.VertxModule2
import com.subha.vertx.service.VertxService
import groovy.util.logging.Slf4j
import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.Vertx

/**
 * Created by user on 12/13/2016.
 */
@Slf4j
class MasterVerticle extends AbstractVerticle {

    static DeploymentOptions deploymentOptions
    static Injector injector
    VertxService vertxService

    @Inject
    MasterVerticle(VertxService vertxService,Injector injector){
        this.vertxService = vertxService
        this.injector = injector
    }

    static {
        deploymentOptions = new  DeploymentOptions().setConfig(new JsonObject()
                .put("http.port.server1", 8081)
                .put("http.port.server2", 8082)
                .put("http.port.server3", 8083));
    }
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        injector = GuiceVertxLauncher.injector

        def verticle2 = injector.getInstance(Server2.class)
        def verticle3 = injector.getInstance(Server3.class)

        vertxService.serve()


        startFuture.setHandler(
                {asyncResult ->
                    if(!asyncResult.succeeded())
                        println("No: ${asyncResult.cause()}")
                    else {
                        println "Yes: ${asyncResult.succeeded()}"

                        vertx.deployVerticle(verticle3, deploymentOptions,
                                { serverAsyncResult ->
                                    println "The Server 3 is: ${serverAsyncResult.succeeded()}"
                                    if(!serverAsyncResult.succeeded())
                                        serverAsyncResult.cause().printStackTrace()
                                }
                        )

                        vertx.deployVerticle(verticle2, deploymentOptions,
                                { server2AsyncResult ->
                                    println "The Server 2 is: ${server2AsyncResult.succeeded()}"
                                    if(!server2AsyncResult.succeeded())
                                        server2AsyncResult.cause().printStackTrace()
                                }
                        )
                    }
                 }
        )

        startFuture.complete()
    }
}

