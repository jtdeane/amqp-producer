FROM openjdk:8u141-jdk-slim
MAINTAINER jeremydeane.net
EXPOSE 9001
RUN mkdir /app/
COPY target/amqp-producer-1.0.1.jar /app/
ENTRYPOINT exec java $JAVA_OPTS -Damqp.hostname='amqp-broker' -jar /app/amqp-producer-1.0.1.jar