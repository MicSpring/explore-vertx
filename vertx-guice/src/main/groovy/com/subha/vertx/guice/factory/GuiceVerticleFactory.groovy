package com.subha.vertx.guice.factory

import com.google.common.base.Preconditions
import com.google.inject.Injector
import io.vertx.core.Verticle
import io.vertx.core.Vertx
import io.vertx.core.impl.verticle.CompilingClassLoader
import io.vertx.core.spi.VerticleFactory

/**
 * Created by user on 12/1/2016.
 */
class GuiceVerticleFactory implements VerticleFactory {

    public static final String PREFIX = "java-guice";
    private final Injector injector;

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
