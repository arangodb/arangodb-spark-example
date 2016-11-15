FROM ubuntu:16.04

RUN echo "deb http://repos.mesosphere.io/ubuntu xenial main" > /etc/apt/sources.list.d/mesosphere.list && \
    apt-key adv --keyserver keyserver.ubuntu.com --recv E56151BF && \
    apt-get update && \
    apt-get -y install mesos openjdk-8-jdk maven


RUN mkdir /arangodb-spark-example
WORKDIR /arangodb-spark-example

ADD src src
ADD pom.xml pom.xml

# Prepare by downloading dependencies
RUN ["mvn", "dependency:resolve", "verify", "package"]
RUN ["cp", "target/arangodb-spark-example-1.0.0-SNAPSHOT-allinone.jar", "/arangodb-spark-example-1.0.0-SNAPSHOT-allinone.jar"]
WORKDIR /
RUN ["rm" ,"-Rf", "/arangodb-spark-example"]

EXPOSE 8080
CMD ["java", "-jar", "/arangodb-spark-example-1.0.0-SNAPSHOT-allinone.jar"]
