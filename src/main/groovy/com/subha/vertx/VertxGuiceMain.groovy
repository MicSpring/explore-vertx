package com.subha.vertx

import com.google.inject.Module
import com.subha.vertx.binder.ServiceBinder
import com.subha.vertx.guice.launcher.GuiceVertxLauncher
import io.vertx.core.Vertx
import org.slf4j.LoggerFactory

/**
 * Created by user on 12/2/2016.
 */
class VertxGuiceMain extends GuiceVertxLauncher{

    static def logger = LoggerFactory.getLogger(VertxGuiceMain)
    static main(args){
        println("The Arguments are:$args")
        new VertxGuiceMain().dispatch(args)
    }

    @Override
    protected List<Module> getModules(Vertx vertx) {
        logger.debug "The Vertx Modules are: $vertx"
        List<Module> modules = super.getModules(vertx);
        modules.add(new ServiceBinder());
        return modules;
    }

}
