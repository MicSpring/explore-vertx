package com.subha.vertx.binder

import com.google.inject.AbstractModule
import com.subha.vertx.service.VertxService
import com.subha.vertx.service.VertxServiceImpl
import org.apache.commons.dbcp2.BasicDataSource

import javax.sql.DataSource

/**
 * Created by user on 11/30/2016.
 */
class ServiceBinder extends AbstractModule{

    DataSource dataSource

    public ServiceBinder(){
        dataSource = new BasicDataSource()
        dataSource.with {
            driverClassName = "org.hsqldb.jdbcDriver"
            url = "jdbc:hsqldb:mem:test?shutdown=true"
            username = "sa"
            password = ""
        }
    }

    @Override
    protected void configure() {
        this.bind(VertxService.class).to(VertxServiceImpl.class)
        this.bind(DataSource)toInstance(this.dataSource)
    }
}
