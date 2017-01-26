package com.subha.vertx.db

import com.couchbase.client.java.env.CouchbaseEnvironment
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment
import com.subha.vertx.properties.CouchbaseEnv
import spock.lang.Specification
/**
 * Created by user on 1/21/2017.
 */
class CouchbaseSpec extends Specification {
    def setupSpec() {}

    def "test couchbase env"(){
        when:
           // CouchbaseEnvironment couchbaseEnvironment = CouchbaseEnv.getCouchbaseEnvironment()
            CouchbaseEnv couchbaseEnv = new CouchbaseEnv({CouchbaseEnvironment couchbaseEnvironment1 -> couchbaseEnvironment1})
           /* Bucket bucket = CouchbaseCluster.create(couchbaseEnvironment,"localhost")
                .openBucket("my-bucket", "pwd")

            bucket.async()
                    .insert(RawJsonDocument.create("groovy","Groovy In Action Second Edition"))
                    .subscribe({rawJsonDocument -> println rawJsonDocument
                    },
                    {error -> error.printStackTrace()
                    },
                    {
                        println "Insertion Completed"
                    }
            )
*/
        then:
            println "The CouchBase env is:" +
                " ${couchbaseEnv.getCouchbaseEnvironment(new DefaultCouchbaseEnvironment.Builder().build())}"
    }

    def cleanupSpec() {}
}
