package com.subha.vertx.guice.factory

import com.google.common.base.Preconditions
import com.google.inject.Injector
import io.vertx.core.Verticle
import io.vertx.core.Vertx
import io.vertx.core.impl.verticle.CompilingClassLoader
import io.vertx.core.spi.VerticleFactory
import org.slf4j.LoggerFactory

/**
 * Created by user on 12/1/2016.
 */
class GuiceVerticleFactory implements VerticleFactory {

    static def logger = LoggerFactory.getLogger(GuiceVerticleFactory)

    private static final String PREFIX = "java-guice"
    private static final String VERTICLE_PREFIX = "Server"

    private final Injector injector

    GuiceVerticleFactory(Injector  injector){
        if(injector)
            this.injector = Preconditions.checkNotNull(injector)
        else
            throw new NullPointerException()
    }


    @Override
    String prefix() {
        return PREFIX
    }

    @Override
    Verticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {
       /* String[] verticleNameArr = verticleName.split("\\^")

         def verticleNamedeployed = verticleNameArr.find { verticle ->
            verticle.contains(VERTICLE_PREFIX)
        }
*/
        println "The Verticle to be deployed is: $verticleName"
        verticleName = VerticleFactory.removePrefix(verticleName)
        Class clazz
        if (verticleName.endsWith(".java")) {
            CompilingClassLoader compilingLoader = new CompilingClassLoader(classLoader, verticleName);
            String className = compilingLoader.resolveMainClassName();
            clazz = compilingLoader.loadClass(className);
        } else {
            clazz = classLoader.loadClass(verticleName);
        }
        return (Verticle) this.injector.getInstance(clazz);

    }
}
