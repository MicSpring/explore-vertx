package com.subha.vertx.guice.launcher

import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Module
import com.subha.vertx.guice.factory.GuiceVerticleFactory
import com.subha.vertx.guice.factory.GuiceVerticleFactory2
import com.subha.vertx.guice.module.VertxModule
import io.vertx.core.DeploymentOptions
import io.vertx.core.Launcher
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory

/**
 * Created by user on 12/1/2016.
 */
class GuiceVertxLauncher extends Launcher{

    static def logger = LoggerFactory.getLogger(GuiceVertxLauncher)
    static def injector;

    static main(args){
        println " ************ Guice Vertx Launcher Called....."
        new GuiceVertxLauncher().dispatch(args)
    }

    @Override
     public void afterStartingVertx(Vertx vertx) {
        println "afterStartingVertx is: $vertx"
        super.afterStartingVertx(vertx)
        GuiceVerticleFactory guiceVerticleFactory = new GuiceVerticleFactory(this.createInjector(vertx));
       // GuiceVerticleFactory2 guiceVerticleFactory2 = new GuiceVerticleFactory2(this.createInjector(vertx));
        vertx.registerVerticleFactory(guiceVerticleFactory)
      //  vertx.registerVerticleFactory(guiceVerticleFactory2)
        println "The Verticle Factories are: ${vertx.verticleFactories()}"
        }

    /*@Override
    void beforeDeployingVerticle(DeploymentOptions deploymentOptions){
        deploymentOptions.setConfig(new JsonObject().put("http.port.server1", 8081).put("http.port.server2", 8082));
    }*/

   /* @Override
    void afterConfigParsed(JsonObject config){
        logger.debug "The Config Object is: ${config.encodePrettily()}"
    }*/

    /*@Override
    void beforeStartingVertx(VertxOptions options){
        logger.debug " The Vertx Options are: $options"
    }*/

    protected Injector createInjector(Vertx vertx){
        if(injector == null) {
            println "******  Creating Injector Instance....."
            injector = Guice.createInjector(this.getModules(vertx))
        }

        injector
    }

    protected List<Module> getModules(Vertx vertx){
        println "Module added for $vertx"
        List<Module> moduleList = new LinkedList<>()
        moduleList.add(new VertxModule(vertx))
        moduleList
    }

}
