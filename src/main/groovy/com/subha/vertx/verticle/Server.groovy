package com.subha.vertx.verticle

import io.vertx.core.Future
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.rx.java.ObservableFuture
import io.vertx.rx.java.RxHelper
import io.vertx.rxjava.core.AbstractVerticle

/**
 * Created by user on 11/9/2016.
 */
class Server extends AbstractVerticle {
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

        Router router = Router.router(vertx);


        //Router 1: To serve all requests(No path been provided in
        //route() method

        router.route('/').handler{routingContext->

            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("content-type", "text/html")
                    .end("<h1>Hello!!! Server Verticle Welcomes You....</h1>");

        }

        //Router 2: To Serve Static Context

        router.route("/assets/*").handler ({
            StaticHandler.create("staticcontents")
        }).failureHandler{ it->
            println "The Failure Handler is: $it"

        }


        /**
         * Using router instead of Request Handler
         */


        vertx.createHttpServer().requestHandler(router.&accept)
                .listen(config().getInteger("http.port",8070),{
            println "The Result is $it"
            if(it.succeeded())
                fut.complete()
            else
                fut.fail(it.cause())
        })

    }
}
