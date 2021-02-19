FROM openjdk:8
COPY jballerina-tools-2.0.0-SNAPSHOT /usr/ballerina/jballerina-tools-2.0.0-SNAPSHOT
COPY benchmark /usr/benchmark
WORKDIR /usr/benchmark
RUN ["/usr/ballerina/jballerina-tools-2.0.0-SNAPSHOT/bin/ballerina","build", "--skip-tests","-a"]
CMD ["/usr/ballerina/jballerina-tools-2.0.0-SNAPSHOT/bin/ballerina","run", "target/bin/main.jar"]
EXPOSE 9090
