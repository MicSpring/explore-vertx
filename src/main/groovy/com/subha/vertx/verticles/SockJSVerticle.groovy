package com.subha.vertx.verticles

import com.subha.vertx.handler.DataHandler
import com.subha.vertx.handler.EventBusHandler
import io.vertx.core.AbstractVerticle
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.ext.web.handler.sockjs.BridgeEventType
import io.vertx.ext.web.handler.sockjs.BridgeOptions
import io.vertx.ext.web.handler.sockjs.PermittedOptions
import io.vertx.ext.web.handler.sockjs.SockJSHandler
import org.slf4j.LoggerFactory

/**
 * Created by user on 12/18/2016.
 */
class SockJSVerticle extends AbstractVerticle {

    def logger = LoggerFactory.getLogger(SockJSVerticle)
     static SockJSHandler sockJSHandler = null

    public void start() throws Exception {
        Router router = Router.router(vertx);

        router.route("/eventbus/*").handler(eventBusHandler())
        router.route("/api/test").handler(new EventBusHandler().&generateSockData)
        //router.route("/eventdata/*").handler(eventBusDataHandler())


        router.route('/*').handler(
                StaticHandler.create())

        vertx.createHttpServer(new HttpServerOptions()
                .setPort(config().getInteger("http.port.server4",8094))
                .setHost("localhost"))
                .requestHandler(router.&accept).listen({
            httpServer->
                if(!httpServer.succeeded())
                    httpServer.cause().printStackTrace()
                else
                    println "Server 4 StrartUp Completed...."
        })
    }

    private SockJSHandler eventBusHandler() {
        BridgeOptions options = new BridgeOptions()
        .addOutboundPermitted(new PermittedOptions().setAddressRegex("auction[\\.0-9]*"));
        //if(sockJSHandler == null)
        sockJSHandler = SockJSHandler.create(vertx).bridge(options, {event ->

            if (event.type() == BridgeEventType.SOCKET_CREATED) {
                println("A socket was created--- with ${event.getClass()}");
            }

            else if (event.type() == BridgeEventType.RECEIVE) {
                println("A Data Received ---");
            }
            else if(event.type() == BridgeEventType.PUBLISH) {
                println("A Data Published ---");
            }
            else if(event.type() == BridgeEventType.SEND) {
                println("A Data Sent ---");
                println "Data is:--${event.rawMessage.toString()} for event: ${event.type().toString()}"
                vertx.eventBus().publish("auction" , "{\"price\":\"777\"}");
            }


            event.complete(true);
        });
       /* else
            sockJSHandler.bridge(options, {event ->

                if (event.type() == BridgeEventType.SOCKET_CREATED) {
                    println("A socket was created****");
                }
                event.complete(true);
            });
        //sockJSHandler.socketHandler(new DataHandler())*/
         sockJSHandler
        }

    private SockJSHandler eventBusDataHandler(){
        BridgeOptions options = new BridgeOptions()
                .addOutboundPermitted(new PermittedOptions().setAddressRegex("send"));
        if(sockJSHandler == null)
            sockJSHandler = SockJSHandler.create(vertx).bridge(options, {event ->

                if (event.type() == BridgeEventType.SOCKET_CREATED) {
                    println("A socket was created 111");
                }
                else if (event.type() == BridgeEventType.RECEIVE) {
                    println("A Data Received");
                }
                else if(event.type() == BridgeEventType.PUBLISH) {
                    println("A Data Published");
                }
                event.complete(true);
            });
        else
            sockJSHandler.bridge(options, {event ->

                if (event.type() == BridgeEventType.SOCKET_CREATED) {
                    println("A socket was created 2222");
                }
                else if (event.type() == BridgeEventType.RECEIVE) {
                    println("A Data Received");
                }
                else if(event.type() == BridgeEventType.PUBLISH) {
                    println("A Data Published");
                }
                event.complete(true);
            });
        //sockJSHandler.socketHandler(new DataHandler())
        return sockJSHandler

    }

}
