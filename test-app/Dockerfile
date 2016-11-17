FROM maven:3.3.9-jdk-8

RUN mkdir /arangodb-spark-example
WORKDIR /arangodb-spark-example

ADD src src
ADD pom.xml pom.xml

# Prepare by downloading dependencies
RUN ["mvn", "dependency:resolve", "verify", "package"]
RUN ["cp", "target/arangodb-spark-example-test-app-1.0.0-SNAPSHOT-standalone.jar", "/arangodb-spark-example-test-app-1.0.0-SNAPSHOT-standalone.jar"]
WORKDIR /
RUN ["rm" ,"-Rf", "/arangodb-spark-example"]

EXPOSE 8080
CMD ["java", "-jar", "/arangodb-spark-example-test-app-1.0.0-SNAPSHOT-standalone.jar"]
