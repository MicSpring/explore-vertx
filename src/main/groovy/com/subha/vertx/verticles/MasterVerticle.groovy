package com.subha.vertx.verticles

import com.google.inject.Inject
import com.google.inject.Injector
import com.subha.vertx.guice.launcher.GuiceVertxLauncher
import com.subha.vertx.service.VertxService
import groovy.util.logging.Slf4j
import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Future
import io.vertx.core.json.JsonObject


import io.vertx.servicediscovery.Record
import io.vertx.servicediscovery.ServiceDiscovery
import io.vertx.servicediscovery.types.HttpEndpoint
import io.vertx.servicediscovery.types.JDBCDataSource

/**
 * Created by user on 12/13/2016.
 */
@Slf4j
class MasterVerticle extends AbstractVerticle {

    static DeploymentOptions deploymentOptions
    static Injector injector
    VertxService vertxService
    ServiceDiscovery serviceDiscovery


    @Inject
    MasterVerticle(VertxService vertxService,Injector injector){
        this.vertxService = vertxService
        this.injector = injector
    }

    static {
        deploymentOptions = new  DeploymentOptions().setConfig(new JsonObject()
                .put("http.port.server1", 8081)
                .put("http.port.server2", 8082)
                .put("http.port.server3", 8083)
                .put("http.port.server4", 8084));
    }
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        injector = GuiceVertxLauncher.injector

        def verticle2 = injector.getInstance(Server2.class)
        def verticle3 = injector.getInstance(Server3.class)
        def verticle4 = injector.getInstance(SockJSVerticle)

        vertxService.serve()

        serviceDiscovery = ServiceDiscovery.create(vertx)

        JsonObject jdbcConfig = new JsonObject()
                .put("url","jdbc:hsqldb:mem:test?shutdown=true")
                .put("driver_class","org.hsqldb.jdbcDriver")
                .put("max_pool_size",3)
                .put("username","sa")
                .put("password","")


        startFuture.setHandler(
                {asyncResult ->
                    if(!asyncResult.succeeded())
                        println("No: ${asyncResult.cause()}")
                    else {
                        println "Yes: ${asyncResult.succeeded()}"

                        vertx.deployVerticle(verticle3, deploymentOptions,
                                { serverAsyncResult ->
                                    println "The Server 3 is: ${serverAsyncResult.succeeded()}"
                                    if(!serverAsyncResult.succeeded())
                                        serverAsyncResult.cause().printStackTrace()
                                }
                        )

                        vertx.deployVerticle(verticle2, deploymentOptions,
                                { server2AsyncResult ->
                                    println "The Server 2 is: ${server2AsyncResult.succeeded()} \n\n " +
                                            "And the class is: ${server2AsyncResult.getClass()}"
                                    if(!server2AsyncResult.succeeded())
                                        server2AsyncResult.cause().printStackTrace()
                                    else{
                                        println "The Server 2 Deployed Successfully... \n\n So, " +
                                                "Publishing the Service..."
                                        publishService(startFuture,serviceDiscovery,"Server2")
                                        publishJDBCService(startFuture,serviceDiscovery,jdbcConfig,"MY-H2-DB")
                                      //  println " #### Not Publising as of Now!!!!"
                                    }
                                }
                        )

                        vertx.deployVerticle(verticle4, deploymentOptions,
                                { server4AsyncResult ->
                                    println "The Server 4 is: ${server4AsyncResult.succeeded()}"
                                    if(!server4AsyncResult.succeeded())
                                        server4AsyncResult.cause().printStackTrace()
                                }
                        )
                    }
                 }
        )

        startFuture.complete()
    }

    private void publishService(Future future, ServiceDiscovery serviceDiscovery,String name){
        Record record = HttpEndpoint.createRecord(name,"localhost",getDeploymentOptions().config.getInteger("http.port.server2",8092),"/dataServer2")
        serviceDiscovery.publish(record,{asyncResultRecord ->
            if(asyncResultRecord.succeeded()){
                println "Location:${asyncResultRecord.result().location} " +
                        "\n Metadata:${asyncResultRecord.result().getMetadata()} " +
                        "\n  Registration:${asyncResultRecord.result().getRegistration()} " +
                        "\n Status: ${asyncResultRecord.result().status}"
                println "Record Syccessfully Published..."
            }
            else{
                "Cannot Publish ${asyncResultRecord.result().name} " +
                        "for ${asyncResultRecord.cause().printStackTrace()}"
            }

        });

    }

    private void publishJDBCService (Future future, ServiceDiscovery serviceDiscovery, JsonObject jdbcConfig, String dbName){
       Record jdbcRecord = JDBCDataSource.createRecord("jdbcDataSource",jdbcConfig, new JsonObject().put("dbName",dbName))

        serviceDiscovery.publish(jdbcRecord,{asyncResultRecord ->
            if(asyncResultRecord.succeeded()){
                println "JDBC Location:${asyncResultRecord.result().location} " +
                        "\nJDBC  Metadata:${asyncResultRecord.result().getMetadata()} " +
                        "\nJDBC   Registration:${asyncResultRecord.result().getRegistration()} " +
                        "\nJDBC  Status: ${asyncResultRecord.result().status}"
                println "JDBC  Record Syccessfully Published..."
            }
            else{
                "Cannot Publish JDBC RECORD ${asyncResultRecord.result().name} " +
                        "for ${asyncResultRecord.cause().printStackTrace()}"
            }

        });
    }
}

