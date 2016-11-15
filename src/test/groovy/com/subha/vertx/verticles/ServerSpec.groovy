package com.subha.vertx.verticles

import spock.lang.Specification

/**
 * Created by user on 11/10/2016.
 */
class ServerSpec extends Specification {
    def "Server Test"() {
        expect:
            name.size() == length

        where:
            name | length
            "Subha" | 5
    }
}
