package com.subha.vertx.guice.factory

import com.google.common.base.Preconditions
import com.google.inject.Injector
import io.vertx.core.Verticle
import io.vertx.core.impl.verticle.CompilingClassLoader
import io.vertx.core.spi.VerticleFactory
import org.slf4j.LoggerFactory

/**
 * Created by user on 12/11/2016.
 */
class GuiceVerticleFactory2 implements VerticleFactory {

    static def logger = LoggerFactory.getLogger(GuiceVerticleFactory2)

    private static final String VERTICLE_PREFIX = "Server2"
    private static final String PREFIX = "java-guice2"

    private final Injector injector

    GuiceVerticleFactory2(Injector  injector){
        if(injector)
            this.injector = Preconditions.checkNotNull(injector)
        else
            throw new NullPointerException()
    }

    @Override
    String prefix() {
        PREFIX
    }

    @Override
    Verticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {
        println "### The Server 2 Verticle Factory is: $verticleName"
        String[] verticleNameArr = verticleName.split("\\^")

        def verticleNameDeployed = verticleNameArr.find { verticle ->
            verticle.contains(VERTICLE_PREFIX)
        }

        println "The Verticle 2 to be deployed is: $verticleNameDeployed"
        verticleNameDeployed = VerticleFactory.removePrefix(verticleNameDeployed)
        Class clazz
        if (verticleNameDeployed.endsWith(".java")) {
            CompilingClassLoader compilingLoader = new CompilingClassLoader(classLoader, verticleNameDeployed);
            String className = compilingLoader.resolveMainClassName();
            clazz = compilingLoader.loadClass(className);
        } else {
            clazz = classLoader.loadClass(verticleNameDeployed);
        }
         (Verticle) this.injector.getInstance(clazz);
    }
}
