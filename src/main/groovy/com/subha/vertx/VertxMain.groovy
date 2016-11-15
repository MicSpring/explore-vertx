package com.subha.vertx

import io.vertx.core.DeploymentOptions
import io.vertx.core.json.JsonObject
import io.vertx.rxjava.core.Vertx
import rx.Observable
import com.subha.vertx.verticles.Server


/**
 * Created by user on 11/8/2016.
 */
class VertxMain /*extends AbstractVerticle*/ {
    static main(args){
        println "Welcome to Vertx World!!!"

        def port = 8081;
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", port)
        );


        Vertx vertx = Vertx.vertx()
        Observable<String> deploymentObservable = vertx.deployVerticleObservable(Server.class.name,options)
        deploymentObservable.subscribe(
                {
                    println "On Next Emission: $it"
                 },
                {
                    println "On Error $it"
                },
                {
                    println "Completed"
                })

    }
}
