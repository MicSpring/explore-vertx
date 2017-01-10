package com.subha.vertx.guice.module

import com.google.inject.AbstractModule
import com.subha.vertx.guice.dependency.DepImpl
import com.subha.vertx.guice.dependency.Dependency
import io.vertx.rxjava.core.Vertx
/**
 * Created by user on 12/11/2016.
 */
class VertxModule2 extends AbstractModule{
    private final Vertx vertx

    VertxModule2(Vertx vertx){
        if(vertx) {
            this.vertx = vertx
        }
        else
            throw new NullPointerException()
    }

    @Override
    protected void configure() {
        this.bind(Vertx).toInstance(this.vertx)
        this.bind(Dependency)to(DepImpl)
    }
}
