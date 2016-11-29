package com.subha.vertx.verticles

import com.google.inject.Inject
import com.subha.vertx.domains.Currency
import com.subha.vertx.domains.Denomination
import com.subha.vertx.service.VertxService
import io.vertx.core.Future
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.core.metrics.Measured
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.rx.java.ObservableFuture
import io.vertx.rxjava.core.AbstractVerticle
import io.vertx.rxjava.core.RxHelper
import io.vertx.rxjava.core.Vertx
import io.vertx.rxjava.ext.jdbc.JDBCClient
import io.vertx.rxjava.ext.sql.SQLConnection
import rx.Observable
import rx.Observer

/**
 * Created by user on 11/9/2016.
 */
class Server extends AbstractVerticle {

    def denominations = createDenominations()

    def vertxService

    @Inject
    public Server(VertxService vertxService){
        this.vertxService   = vertxService
    }

    public Server(){

    }

    @Override
    public void start(Future<Void> fut) throws Exception {
        /*vertx.createHttpServer().requestHandler({req->
            req.response().end("<h1>Hello!!! Server Verticle welcomes You....</h1>")
        }).listen(config().getInteger("http.port",8070),{
            println "The Result is $it"
            if(it.succeeded())
                fut.complete()
            else
                fut.fail(it.cause())
        })
*/
        println "#### The Dependency is: ${vertxService.serve()}"

        //JDBC properties configuration
        JsonObject jdbcConfig = new JsonObject()
                .put("url","jdbc:hsqldb:mem:db?shutdown=true")
                .put("_driverclass ","org.hsqldb.jdbcDriver")


        def connection = null;
        //JDBCClient jdbcClient = JDBCClient.createShared(vertx,jdbcConfig)
        JDBCClient jdbcClient = JDBCClient.createShared(Vertx.vertx() ,jdbcConfig)
        Observable<SQLConnection> connectionObservable = jdbcClient.getConnectionObservable()
        connectionObservable.subscribe(
                {conn ->
                    println "The Connection is $conn"
                    connection = conn
                }
                ,
                {error ->
                    error.printStackTrace()
                },
                {
                    println "JDBC coonection completed"
        }

    )

       /* jdbcClient.getConnection{handler ->
           if(handler.failed())
               println "Failure for:${handler.cause().getMessage()}"
            else {
               connection = handler.result()
               connection.execute "CREATE TABLE IF NOT EXISTS Whisky (id INTEGER IDENTITY, name varchar(100), " +
                       "origin varchar(100))",{result1 ->
                   if(result1.failed())
                       return
               }

               connection.query "SELECT * FROM Whisky",{selectResult->
                   println "No of Rows: ${selectResult.result().numRows}"

               }
           }
        }*/


       /* rxJdbcClient.getConnectionObservable().subscribe({
            conn -> println "The conn is: $conn"
                println "Doneee"
        },{
            error -> error.printStackTrace()
        },
                {
                    println "Just Completed....."
                })*/


        Router router = Router.router(vertx);


        //Router 1: To serve all requests(No path been provided in
        //route() method

        router.route(HttpMethod.GET,'/').handler{ routingContext->

            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("content-type", "text/html")
                    .end("<h1>Hello!!! Server Verticle Welcomes You....</h1>");

        }

        //Router 2: To Serve Static Context

        router.route('/assets/*').handler(
            StaticHandler.create('statics'))
        /*.failureHandler{ it->
            println "The Failure Handler is: $it"

        }*/

        //Router 3: To serve all requests(No path been provided in
        //route() method

        router.route(HttpMethod.GET,'/:denomination').handler(this.&getDetails)


        /**
         * Using router instead of Request Handler to start the server
         */
       /* vertx.createHttpServer().requestHandler(router.&accept)
                .listen(config().getInteger("http.port",8070),{
            println "The Server Start Status: $it"
            if(it.succeeded())
                fut.complete()
            else
                fut.fail(it.cause())
        })*/

        //Rxified Version of the Vertx Server

        //UserObservable Handler

        /*def serverHandler = io.vertx.rx.java.RxHelper.observableHandler()
        serverHandler.subscribe{
            status ->   if(status.succeeded())
                            fut.complete()
                        else
                            fut.fail(status.cause())
        }*/

        //Using Future Handler

        /*ObservableFuture<HttpServer> serverFuture = io.vertx.rx.java.RxHelper.observableFuture();
        serverFuture.subscribe(
                { io.vertx.core.http.HttpServer server -> println "The Server Start Status: $server "},
                {failure -> println "The Failure Status: $failure"}
        )*/

        //Using Observer
        Observer<HttpServer> observer = new Observer<HttpServer>() {
            @Override
            void onCompleted() {
                println "Server Start Completed....."

            }

            @Override
            void onError(Throwable e) {
                e.printStackTrace()
            }

            @Override
            void onNext(HttpServer httpServer) {
                println "The Server Start Status: $httpServer "

            }
        }

        def observerHandler = io.vertx.rx.java.RxHelper.toFuture(observer);

        vertx.createHttpServer(
                new HttpServerOptions().setPort(config().getInteger("http.port",8070))
                .setHost("localhost"))
                .requestHandler(router.&accept)
                .listen(observerHandler)


    }


    private Denomination createDenominations(){
        Currency currency_100 = new Currency(value: "100",serial: ["qwer123","dfg45678"] as String[])
        Currency currency_500 = new Currency(value: "500",serial: ["qwer999","dfg49999"] as String[])
        Denomination denomination = new Denomination(currency: [currency_100,currency_500] as Currency[])
        return denomination
    }

    private List<Currency> getDetails(RoutingContext routingContext){
        def curValue = routingContext.pathParam("denomination")
        List<Currency> currencyList = new ArrayList<>()

        denominations.currency.each { curr ->
            if(curValue == curr.value)
                currencyList.add(curr)
        }
        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(currencyList?:"Currency Not Present in Denominations"))
    }
}
