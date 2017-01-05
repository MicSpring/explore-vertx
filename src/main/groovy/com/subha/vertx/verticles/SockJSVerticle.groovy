package com.subha.vertx.verticles

import com.subha.vertx.handler.EventBusHandler
import com.subha.vertx.handler.WebSocketHandler
import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpClientOptions
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
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
    def WebSocketFrameHandler;
     static SockJSHandler sockJSHandler = null

    public void start() throws Exception {
        Router router = Router.router(vertx);

        router.route("/eventbus/*").handler(eventBusHandler())
        router.route("/api/test").handler(new EventBusHandler().&generateSockData)
        //router.route("/eventdata/*").handler(eventBusDataHandler())
        router.route("/sendToSocket").handler({context->sendSocketData(context,vertx)})


        router.route('/*').handler(
                StaticHandler.create())

        HttpServer httpServer1 =vertx.createHttpServer(new HttpServerOptions()
                .setPort(config().getInteger("http.port.server4",8094))
                .setHost("localhost"));

        httpServer1.websocketHandler(new WebSocketHandler());

        httpServer1.requestHandler(router.&accept).listen({
            httpServer->
                if(!httpServer.succeeded())
                    httpServer.cause().printStackTrace()
                else {
                    println "Server 4 StrartUp Completed...."
                }
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

    private static void sendSocketData(RoutingContext routingContext, Vertx vertx){
            HttpClient httpClient = vertx.createHttpClient(new HttpClientOptions())
            httpClient.websocketStream(8084,"localhost","/sendToSocket")
                    .exceptionHandler({t->t.printStackTrace()})
                    .handler({websocket->
                                websocket.setWriteQueueMaxSize(Integer.MAX_VALUE)
                                        .closeHandler({__ -> println "***** The WebSocket Close Handler is Invoked..."})
                                        .write(Buffer.buffer("Send Data to Server...".toUpperCase().getBytes()))
                                        .exceptionHandler({ex->ex.printStackTrace()})
                                        //.handler({buffer->"The Data as obtained from the sever is:${new String(buffer.bytes)}"})
                                        .frameHandler({webSocketFrame -> println "The Data at the Client Socket is:${webSocketFrame.textData()}"})
                                        .close()
                            })


        routingContext.response().setStatusCode(200).end()
    }

}
