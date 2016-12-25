package com.subha.vertx.verticles

import com.google.inject.Inject
import com.subha.vertx.guice.dependency.Dependency
import com.subha.vertx.handler.DataHandler
import io.vertx.circuitbreaker.CircuitBreaker
import io.vertx.circuitbreaker.CircuitBreakerOptions
import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpClientResponse
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.JsonObject
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.sql.SQLConnection
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.ext.web.handler.sockjs.SockJSHandler
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions
import io.vertx.servicediscovery.ServiceDiscovery
/**
 * Created by user on 12/14/2016.
 */
class Server3 extends AbstractVerticle{

    Dependency dependency
    ServiceDiscovery serviceDiscovery
    CircuitBreakerOptions circuitBreakerOptions
    CircuitBreaker circuitBreaker

    @Inject
    public Server3(Dependency dependency){
        this.dependency = dependency
    }

    public void start() throws Exception {

        serviceDiscovery = ServiceDiscovery.create(vertx)
        println " **** The Dependency is: ${dependency.serve()}"


        def sockJSHandlerOptions = new SockJSHandlerOptions().setHeartbeatInterval(3000)

        def sockJSHandler = SockJSHandler.create(vertx,sockJSHandlerOptions)
        sockJSHandler.socketHandler(new DataHandler())

        circuitBreakerOptions = new CircuitBreakerOptions()
                .setMaxFailures(5) // number of failure before opening the circuit
                .setFallbackOnFailure(true) // do we call the fallback on failure
                .setResetTimeout(10000)

        circuitBreaker = CircuitBreaker.create("InvokeServer2-CB",vertx,circuitBreakerOptions)

        def router = Router.router(vertx)

        router.route(HttpMethod.GET,'/test').handler(sockJSHandler)
        router.route(HttpMethod.GET,'/getServer2').handler({routingContext->
            println " ***** Invoking Server2 with Service Discovery ***** "
            invokeServer2(routingContext,serviceDiscovery,circuitBreaker);})

        router.route(HttpMethod.GET,'/getJDBC').handler({routingContext->
            println " ***** Fetching JDBC with Service Discovery ***** "
            fetchJDBC(routingContext,serviceDiscovery,circuitBreaker);})

        router.route('/*').handler(
                StaticHandler.create())

        vertx.createHttpServer(new HttpServerOptions()
                .setPort(config().getInteger("http.port.server3",8093))
                .setHost("localhost"))
                .requestHandler(router.&accept).listen({
            httpServer->
                if(!httpServer.succeeded())
                    httpServer.cause().printStackTrace()
                else
                    println "Server 3 StrartUp Completed...."
        })
    }

    private void invokeServer2(RoutingContext context, ServiceDiscovery serviceDiscovery, CircuitBreaker cktBreaker){
        serviceDiscovery.getRecord(new JsonObject().put("name", "Server2"), { ar ->
            if (ar.succeeded() && ar.result() != null) {
                // Retrieve the service reference
                def reference = serviceDiscovery.getReference(ar.result())
                // Retrieve the service object
                def client = reference.get()
                println "The Client class is: $client"

                // You need to path the complete path

                circuitBreaker.openHandler({
                    v1 -> println " @@@@@ Circuit Open @@@@@  "
                })
                        .closeHandler({v2 -> println " @@@@@ Circuit Closed @@@@@ "})
                .halfOpenHandler({v3 -> println " @@@@@ Circuit Half Opened @@@@@ "})

                circuitBreaker.executeWithFallback({future->

                    client.getNow("/dataServer2", { HttpClientResponse response ->

                        if(response.statusCode() == 200) {
                            response.bodyHandler({ buffer ->
                                println "##### The BodyHandler is:${buffer.toString()}"
                                client.close();
                                reference.release()
                                future.complete(buffer.toString())
                            })
                        }
                        else{
                            future.fail("HTTP error with status Code ${response.statusCode()}" +
                                    " with Message ${response.statusMessage()}");
                        }

                    })
                },{ex ->
                    ex.printStackTrace()
                    return "Circuit Opened....."
                })
            }
            else{
                println("##### Alas!!! Server2 Not Found.....")
            }
        })
        context.response().setStatusCode(200).end()
    }

    private void fetchJDBC (RoutingContext context, ServiceDiscovery serviceDiscovery, CircuitBreaker cktBreaker) {
        serviceDiscovery.getRecord(new JsonObject().put("name", "jdbcDataSource"), { ar ->
            if (ar.succeeded() && ar.result() != null) {
                // Retrieve the service reference
                def reference = serviceDiscovery.getReferenceWithConfiguration(
                        ar.result(), // The record
                        new JsonObject()/*.put("user","sa").put("password","")*/);

                // Retrieve the service object
                JDBCClient jdbcClient = reference.get();
                println "The JDBC Client is:$jdbcClient"
                jdbcClient.getConnection({asyncResult->

                    if(asyncResult.succeeded()) {
                        SQLConnection sqlConnection = asyncResult.result()

                        sqlConnection.execute("CREATE TABLE IF NOT EXISTS Whisky (id INTEGER IDENTITY, name varchar(100), " +
                                "origin varchar(100))", { result ->
                            println "1:${result.result()}"
                        })

                        sqlConnection.execute("Insert into Whisky (id,name,origin) values (1,'Black Label','Scotch')", { result2 ->
                            println "2:${result2.result()}"

                        })

                        sqlConnection.close()
                    }
                    else{
                        println("Connection Not Obtained for:${asyncResult.failed()}")
                    }
                })

                // ...

                // when done
                reference.release();
            }

        })
    }
}
