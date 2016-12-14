package com.subha.vertx.guice.module

import com.google.common.base.Preconditions
import com.google.inject.AbstractModule
import com.google.inject.Injector
import com.subha.vertx.guice.dependency.DepImpl
import com.subha.vertx.guice.dependency.Dependency
import io.vertx.core.Vertx

/**
 * Created by user on 12/1/2016.
 */
class VertxModule extends AbstractModule{

    private final Vertx vertx
   // private final Injector injector

    VertxModule(Vertx vertx/*, Injector injector*/){
        if(vertx) {
            this.vertx = vertx
            //this.injector = injector
        }
        else
            throw new NullPointerException()
    }

    @Override
    protected void configure() {
        this.bind(Vertx).toInstance(this.vertx)
        this.bind(Dependency).to(DepImpl)
        //this.bind(Injector).to(injector)
    }
}
