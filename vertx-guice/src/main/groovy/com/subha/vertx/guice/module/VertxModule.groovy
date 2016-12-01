package com.subha.vertx.guice.module

import com.google.common.base.Preconditions
import com.google.inject.AbstractModule
import com.subha.vertx.guice.dependency.DepImpl
import com.subha.vertx.guice.dependency.Dependency
import io.vertx.core.Vertx

/**
 * Created by user on 12/1/2016.
 */
class VertxModule extends AbstractModule{

    private final Vertx vertx

    VertxModule(Vertx vertx){
        if(vertx)
            this.vertx = vertx
        else
            throw new NullPointerException()
    }

    @Override
    protected void configure() {
        this.bind(Vertx).toInstance(this.vertx)
        this.bind(Dependency)to(DepImpl)
    }
}
