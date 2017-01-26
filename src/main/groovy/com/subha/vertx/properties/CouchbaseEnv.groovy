package com.subha.vertx.properties

import groovy.transform.ToString
/**
 * Created by user on 1/21/2017.
 */
@ToString
class CouchbaseEnv {

    def couchbaseEnvironmentClosure

    public CouchbaseEnv(def couchbaseEnvironment) {
        this.couchbaseEnvironmentClosure = couchbaseEnvironment;
    }

    public def  getCouchbaseEnvironment(def env) {
        couchbaseEnvironmentClosure(env)
    }
}
