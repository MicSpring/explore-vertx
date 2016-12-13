package com.subha.vertx

import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Module
import com.subha.vertx.binder.ServiceBinder
import com.subha.vertx.guice.launcher.GuiceVertxLauncher
import com.subha.vertx.guice.module.VertxModule
import com.subha.vertx.guice.module.VertxModule2
import com.subha.vertx.verticles.Server2
import io.vertx.core.DeploymentOptions
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.SLF4JLogDelegateFactory
import io.vertx.rxjava.core.Vertx
import org.slf4j.LoggerFactory
import rx.Observable
import com.subha.vertx.verticles.Server


/**
 * Created by user on 11/8/2016.
 */
class VertxMain /*extends AbstractVerticle*/ {
    static def logger = LoggerFactory.getLogger(VertxMain)

    static Injector injector
    static main(args) {

        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory")
        logger.debug("Welcome to Vertx World!!!")


        def port = 8081;
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", port).put("guice_binder", ServiceBinder.class.name)
        );


        Vertx vertx = Vertx.vertx()
        injector = Guice.createInjector(getModules(vertx))
        Observable<String> deploymentObservable = vertx.deployVerticleObservable(/*"java-guice:" + */ Server.class.name, options)
        deploymentObservable.subscribe(
                {
                    println "On Next Emission: $it"
                },
                {
                    println "On Error ${it.printStackTrace()}"
                },
                {
                    println "Completed"
                })

        io.vertx.core.Vertx.vertx().deployVerticle(Server2.class.name, options,
                { asyncResult ->
                    println "The result2 is: ${asyncResult.succeeded()}"
                    if(!asyncResult.succeeded())
                        println " The Error is: ${asyncResult.cause()}"
                }
        )

    }


    protected static List<Module> getModules(Vertx vertx) {

        println "The Modules for Vertx: $vertx"
        List<Module> moduleList = new LinkedList<>()
        moduleList.add(new VertxModule2(vertx))
        moduleList.add(new ServiceBinder());
        moduleList

    }
}
