package com.subha.vertx

import com.subha.vertx.binder.ServiceBinder
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
    static main(args){

        System.setProperty("vertx.logger-delegate-factory-class-name","io.vertx.core.logging.SLF4JLogDelegateFactory")
        logger.debug("Welcome to Vertx World!!!")

        def port = 8081;
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", port).put("guice_binder",ServiceBinder.class.name)
        );


        Vertx vertx = Vertx.vertx()
        Observable<String> deploymentObservable = vertx.deployVerticleObservable("java-guice:" + Server.class.name,options)
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

    }
}
