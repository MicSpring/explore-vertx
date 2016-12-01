package com.subha.vertx.binder

import com.google.inject.AbstractModule
import com.subha.vertx.service.VertxService
import com.subha.vertx.service.VertxServiceImpl

/**
 * Created by user on 11/30/2016.
 */
class ServiceBinder extends AbstractModule{

    @Override
    protected void configure() {
        this.bind(VertxService.class).to(VertxServiceImpl.class)
    }
}
