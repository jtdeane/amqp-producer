amqp-producer
=======================
Demo asynchronous request-reply via RabbitMQ (AMQP)

Built with Java 8+, Spring-Boot (2.0.3.RELEASE)

##Spring

* Build

`mvn clean install`

* Start RabbitMQ

* Create VHost cogito

* Create User producer/producer w/ admin role

* Set client VHosts permissions (Read,Write,Configure): / and cogito

* Run

`mvn spring-boot:run -Drun.arguments="-Xmx256m,-Xms128m"`

Check health

`http://localhost:9001/actuator/info`

##Docker

* Create Network

`docker network create amqp-network`

* Start RabbitMQ

`docker run -d -p 15672:15672 --net=amqp-network --name amqp-broker --hostname amqp-broker rabbitmq:3.6.12-management`

* Create VHost cogito

* Create User producer/producer w/ admin role

* Set client VHosts permissions (Read,Write,Configure): / and cogito

* Pull down Image

`docker pull jtdeane/amqp-producer`

OR

* Build locally

`docker build -t amqp-producer:latest .`

* Run Docker

`docker run -d -p 9001:9001 -e JAVA_OPTS='-Xmx256m -Xms128m' --net=amqp-network --hostname amqp-producer amqp-producer:latest` 