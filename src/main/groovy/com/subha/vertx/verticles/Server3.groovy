package com.subha.vertx.verticles

import com.github.davidmoten.rx.jdbc.Database
import com.github.davidmoten.rx.jdbc.tuple.Tuple2
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
import rx.Observable

import javax.sql.DataSource
/**
 * Created by user on 12/14/2016.
 */
class Server3 extends AbstractVerticle{

    Dependency dependency
    ServiceDiscovery serviceDiscovery
    CircuitBreakerOptions circuitBreakerOptions
    CircuitBreaker circuitBreaker
    DataSource dataSource

    @Inject
    public Server3(Dependency dependency, DataSource dataSource){
        this.dependency = dependency
        this.dataSource = dataSource;
    }

    public void start() throws Exception {

        serviceDiscovery = ServiceDiscovery.create(vertx)
        println " **** The Dependency is: ${dependency.serve()}"
        println " **** The DataSource is: $dataSource with Details:${dataSource.getConnection()}"


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

                println "The reference Object is:${reference.getClass()}"
                // Retrieve the service object
                JDBCClient jdbcClient = reference.get();
                println "The JDBC Client is:$jdbcClient"
                jdbcClient.getConnection({asyncResult->

                    if(asyncResult.succeeded()) {
                        SQLConnection sqlConnection = asyncResult.result()

                        sqlConnection.execute("CREATE TABLE IF NOT EXISTS Whisky (id INTEGER IDENTITY, name varchar(100), " +
                                "origin varchar(100))", { result ->
                            println "Table Creation Success:${result.succeeded()}"
                            println "Table Creation Failed:${result.failed()}"
                        })

                        sqlConnection.execute("Insert into Whisky (id,name,origin) values (1,'Black Label','Scotch')", { result2 ->
                            println "Insertion Success:${result2.succeeded()}"
                            println "Insertion Failed:${result2.failed()}"

                        })

                        sqlConnection.commit({commitResult->
                            println "Commit Result Successful:${commitResult.succeeded()}"
                        })
                        sqlConnection.query("Select * from Whisky",{queryResult->
                            if(queryResult.succeeded()){
                                io.vertx.ext.sql.ResultSet resultSet = queryResult.result()
                                println "Now of Rows in Output: ${resultSet.numRows}"
                                resultSet.rows.forEach({jsonObject->
                                    println jsonObject.toString()
                                })

                            }
                            else{
                                println "QueryResult Failed for:${queryResult.failed()}" +
                                "\n ${queryResult.cause()}" +
                                        "\n StackTarce:${queryResult.cause().printStackTrace()}"

                            }

                        })

                        sqlConnection.close()
                    }
                    else{
                        println("Connection Not Obtained for:${asyncResult.failed()}" +
                                "\n ${asyncResult.cause()}\n StackTarce:${asyncResult.cause().printStackTrace()}")
                    }
                })

                /**
                 * Since Fetching SQLConnection are within Asynchronous Handlers so,
                 * Closing JDBC client or Service Reference before the SQL Connection
                 * fetched CAUSES "java.sql.SQLException: An SQLException was
                 * provoked by the following failure: java.lang.InterruptedException"
                 */
                // ...
               // println "Closing JDBC CLIENT"
                //jdbcClient.close()
               // println "JDBC CLIENT Closed"
                // when donelosing JDBC cLIENT
                //reference.release()

                Database db =  Database.fromDataSource(dataSource)
                db.update("create table Student (id integer primary key, name varchar(50), school varchar(50))")
                    .count()
                        .compose(


                db.update("insert into Student(id, name, school) values (:id,:name,:school)")
                        .parameter("id",1)
                         .parameter("name","Subha")
                        .parameter("school","Margaret")
                        .dependsOnTransformer()
                ).compose(

                db.update("insert into Student(id, name, school) values (:id,:name,:school)")
                        .parameter("id",2)
                        .parameter("name","Subha2")
                        .parameter("school","Margaret2")
                        .dependsOnTransformer())
                      .compose(

               db.select("select id from Student ")
                    .dependsOnTransformer()
                    .getAs(Integer)
                ).compose(
                        db.select("SELECT NAME, SCHOOL FROM STUDENT WHERE ID=?")
                        .parameterTransformer()
                        .get({rs-> println "Student Name: ${rs.getString(1)} \n\n Student School:${rs.getString(2)}"})
                ).subscribe(
                        {data-> println "The Data is: ${data.getClass()}"},
                        {ex ->
                           // Exceptions.propagate(ex)
                            ex.printStackTrace()
                            return Observable.error(ex)
                        }
                )

                db.select("SELECT NAME,SCHOOL FROM STUDENT WHERE ID = ? AND SCHOOL = ?")
                .parameters(Observable.just(1,"Margaret", 2, "Margaret2"))
                .getTupleN(Tuple2).subscribe(
                        {tuple-> println "****** The Tuple is: ${tuple}"},
                        {ex-> ex.printStackTrace()}

                )
                /*db.select("select name from Student where id=?")
                        .parameters(studCnt)
                        .getAs(String).subscribe(
                        {studStr -> println "##### The Student String is:$studStr"}
                )*/

            }

        })

        context.response().setStatusCode(200).setStatusMessage("JDBC Operations Successful").end()
    }
}
