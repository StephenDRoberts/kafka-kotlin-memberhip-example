# Demo membership app with Kafka, Kotlin and Docker

Simple example application that allows users to sign up to the site.

Users will create a username, email and password with details being placed onto a kafka topic. A kafka streams processor will subscribe to that topic, hash the passowrd and store the details to a state store. A GET enpoint will be available for a user to get a full list of users who have signed up to the application.

### Technologies used:
* [Kafka](https://kafka.apache.org/intro)
* [Kotlin](https://kotlinlang.org/)
* [Docker](https://www.docker.com/)
* [Spring](https://spring.io/)
* [JUnit](https://junit.org/junit5/)

### Architecture

![alt text](https://github.com/StephenDRoberts/kafka-kotlin-memberhip-example/ArchitectureDiagram.png?raw=true)



### Notes:
#### Creating a POST endpoint
* Needed to add a REST Controller Spring annotation which didn't come with my default package. Imported spring-boot-web-starter into my dependencies.


