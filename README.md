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

![alt text](https://github.com/StephenDRoberts/kafka-kotlin-memberhip-example/blob/master/ArchitectureDiagram.png?raw=true)



### Notes:
#### Creating a POST endpoint
* Needed to add a REST Controller Spring annotation which didn't come with my default package. Imported spring-boot-web-starter into my dependencies.

#### Getting all users over a distributed system
* The current setup for kafka is for the data to be split over 2 partitions. If only one application ran then it would be assigned to both partitions, meaning that when we requested to get all users then it would query both partitions. 
* However, having two applications running, one partition would be assigned to one application. This means that if we requested to get all items from one application, we would only receive the items contained in the partition assinged to it.
* To get around this issue, we have split our GET request into two sections:
  1. Get local users by querying the current applications state store within its assigned partition;
  2. Proxy to a remote address for the users stored in remote partitions.

* To implement this we first query the local store for its items. Then filter out the host & port combination that we've just used. For each host/port left in the metadata for assigned applications, we proxy a request using `restTemplate.exchange()` which fetches local items to that state store/application.
