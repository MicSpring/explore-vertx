import ch.qos.logback.classic.AsyncAppender
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.FileAppender
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy

import static ch.qos.logback.classic.Level.DEBUG

appender("FILE",RollingFileAppender){
    file = "logs/logfile.log"
    rollingPolicy(TimeBasedRollingPolicy){
        fileNamePattern = "logs/logfile.log%d{yyyy-MM-dd}.log"
        maxHistory = 30
        maxFileSize = "1MB"
    }
    encoder(PatternLayoutEncoder) {
        pattern = "%msg%n"
    }
}

appender("ASYNC",AsyncAppender){
    appenderRef('FILE')
}

logger("com.subha", DEBUG,["ASYNC"])
root(DEBUG, ["ASYNC"])



