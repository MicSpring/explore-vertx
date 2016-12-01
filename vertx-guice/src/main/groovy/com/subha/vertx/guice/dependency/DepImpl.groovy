package com.subha.vertx.guice.dependency

/**
 * Created by user on 12/2/2016.
 */
class DepImpl implements Dependency{
    @Override
    String serve() {
        ("*** Dependecy Injected *** ")
    }
}
