FROM maven:3-jdk-8

RUN mkdir /arangodb-spark-example
WORKDIR /arangodb-spark-example

ADD src src
ADD pom.xml pom.xml

# Prepare by downloading dependencies
RUN ["mvn", "dependency:resolve"]  
RUN ["mvn", "verify"]
RUN ["mvn", "package"]

EXPOSE 8080
CMD ["java", "-jar", "target/arangodb-spark-example-1.0.0-SNAPSHOT-standalone.jar"]
