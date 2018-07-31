import ch.qos.logback.classic.encoder.PatternLayoutEncoder

def logLevel = DEBUG
def appenderList = []
appenderList.add("CONSOLE")

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} %-5level [traceId=%X{traceId}] [%thread] [%logger{0}] %msg%n"
    }
}

root(logLevel, appenderList)

