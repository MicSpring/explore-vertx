package com.subha.vertx.verticles

import com.google.inject.Inject
import com.subha.vertx.guice.dependency.Dependency
import com.subha.vertx.service.VertxService
import io.vertx.core.Future
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.Router
import io.vertx.rxjava.core.AbstractVerticle
import org.slf4j.LoggerFactory
import rx.Observer

/**
 * Created by user on 12/11/2016.
 */
class Server2 extends AbstractVerticle {

    static def logger = LoggerFactory.getLogger(Server2)

    Dependency dependency
    VertxService vertxService

    @Inject
    public Server2(Dependency dependency, VertxService vertxService) {
        this.dependency = dependency
        this.vertxService = vertxService
    }

    @Override
    public void start(Future<Void> fut) throws Exception {

        println "#### The Dependency in Server2 is: ${dependency.serve()}"
        vertxService.serve()

        Router router = Router.router(vertx);

        router.route(HttpMethod.GET,'/').handler{ routingContext->

            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("content-type", "text/html")
                    .end("<h1>Hello!!! Server Verticle 2 Welcomes You....</h1>");

        }

        Observer<HttpServer> observer = new Observer<HttpServer>() {
            @Override
            void onCompleted() {
                println "Server 2 Start Completed....."

            }

            @Override
            void onError(Throwable e) {
                e.printStackTrace()
            }

            @Override
            void onNext(HttpServer httpServer) {
                println "The Server 2 Start Status: $httpServer "

            }
        }

        def observerHandler = io.vertx.rx.java.RxHelper.toFuture(observer);

        vertx.createHttpServer(
                new HttpServerOptions().setPort(config().getInteger("http.port.server2",8092))
                        .setHost("localhost"))
                .requestHandler(router.&accept)
                .listen(observerHandler)
    }
}
