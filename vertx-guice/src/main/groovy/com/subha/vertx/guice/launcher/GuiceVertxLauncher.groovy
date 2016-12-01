package com.subha.vertx.guice.launcher

import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Module
import com.subha.vertx.guice.factory.GuiceVerticleFactory
import com.subha.vertx.guice.module.VertxModule
import io.vertx.core.Launcher
import io.vertx.core.Vertx

/**
 * Created by user on 12/1/2016.
 */
class GuiceVertxLauncher extends Launcher{
    static main(args){
        println " ************ Guice Vertx Launcher Called....."
        new GuiceVertxLauncher().dispatch(args)
    }

    @Override
     public void afterStartingVertx(Vertx vertx) {
        println "The Vertx xlass is2 $vertx"
        super.afterStartingVertx(vertx)
        GuiceVerticleFactory guiceVerticleFactory = new GuiceVerticleFactory(this.createInjector(vertx));
        vertx.registerVerticleFactory(guiceVerticleFactory);
        }


    protected Injector createInjector(Vertx vertx){
        Guice.createInjector(this.getModules(vertx))
    }

    protected List<Module> getModules(Vertx vertx){
        println "The Vertx xlass is3 $vertx"
        List<Module> moduleList = new LinkedList<>()
        moduleList.add(new VertxModule(vertx))
        moduleList
    }

}
