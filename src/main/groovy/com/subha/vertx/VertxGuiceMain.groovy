package com.subha.vertx

import com.google.inject.Module
import com.subha.vertx.binder.ServiceBinder
import com.subha.vertx.guice.launcher.GuiceVertxLauncher
import io.vertx.core.Vertx

/**
 * Created by user on 12/2/2016.
 */
class VertxGuiceMain extends GuiceVertxLauncher{

    static main(args){
        new VertxGuiceMain().dispatch(args)
    }

    @Override
    protected List<Module> getModules(Vertx vertx) {
        println "The Vertx xlass is1 $vertx"
        List<Module> modules = super.getModules(vertx);
        modules.add(new ServiceBinder());
        return modules;
    }

}
