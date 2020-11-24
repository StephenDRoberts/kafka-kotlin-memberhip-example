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

![alt text](https://github.com/StephenDRoberts/kafka-kotlin-memberhip-example/assets/blob/master/ArchitectureDiagram.png?raw=true)

#### User Controller
* Directs requests to the corresponding function within UserService/UserRepository.
* Handles requests by using [Spring's RequestMapping annotation](https://www.baeldung.com/spring-new-requestmapping-shortcuts).

#### User Service
* Just a pass through...

#### User Repository
* Handles the creation os new users, utilising the KafkaProducer and the retrieval of users from the state-store (see further information below on handling retrival from a distributed system).

#### KafkaProducer
* Receives a User object to put into the state store.
* Assigns a new UUID for the user and uses Spring Kafkas high level language [KafkaTemplate](https://docs.spring.io/spring-kafka/api/org/springframework/kafka/core/KafkaTemplate.html) to send our User item to the "user-topic".
* NB: The "user-topic" is created from the [docker-compose file](https://github.com/StephenDRoberts/kafka-kotlin-memberhip-example/blob/master/docker-compose.yaml).

#### User Topology
* This is the main Kakfa streams file for manipulating the data read from the "user-topic" and sent to the state store. 
* The topology reads from a given topic, manipulates some data within Kafka Streams and then sends the manipulated data to an in-memory state store.
* The topology uses the [StreamBuilder](https://kafka.apache.org/23/javadoc/org/apache/kafka/streams/StreamsBuilder.html) high level Kafka Streams DSL to create a Stream.
* The name of the topic you wish to read from is provided to the `stream()` method, along with serialisation configuration.
* We have then instructed the stream to manipulate each record it receives to hash the user pasword within a `map()` function.
* Finally we store the KeyValue pair created to a KTable. We use the `Materialized.as` notation to create a new inMemoryKeyValueStore and provide it with serialisation configuration for storage.
 
#### State store query
* This component is responsible for providin a local state store and being able to query its data.
* It utilises the [StreamBuilderFactoryBean](https://docs.spring.io/spring-kafka/docs/current/api/org/springframework/kafka/config/StreamsBuilderFactoryBean.html) that gives more control over the Kafka Streams instance.
* We use the `kafkaStreams.store()` methods and provide in `StoreQueryParameters` with the required state store name and types that the data is store in.
* The state store query is called from within the UserRepository component. In that component we call `store.getStore().all()` to request all items back from the state store.

### Notes:
#### Creating a POST endpoint
* Needed to add a REST Controller Spring annotation which didn't come with my default package. Imported spring-boot-web-starter into my dependencies.

#### Getting all users over a distributed system
* The current setup for kafka is for the data to be split over 2 partitions. If only one application ran then it would be assigned to both partitions, meaning that when we requested to get all users then it would query both partitions. 

![alt text](https://github.com/StephenDRoberts/kafka-kotlin-memberhip-example/assets/blob/master/OneAppDiagram.png?raw=true)

* However, having two applications running, one partition would be assigned to one application. This means that if we requested to get all items from one application, we would only receive the items contained in the partition assinged to it.

![alt text](https://github.com/StephenDRoberts/kafka-kotlin-memberhip-example/assets/blob/master/TwoAppDiagram.png?raw=true)

* To get around this issue, we have split our GET request into two sections:
  1. Get local users by querying the current applications state store within its assigned partition;
  2. Proxy to a remote address for the users stored in remote partitions.

* To implement this we first query the local store for its items. Then filter out the host & port combination that we've just used. For each host/port left in the metadata for assigned applications, we proxy a request using `restTemplate.exchange()` which fetches local items to that state store/application.

#### Conflicting dependencies
Throughout the project I had issues with conflicting dependencies and getting errors such as `NoSuchMethodError` and `NoSuchClassError`. To get passed these issues we needed to run `./gradlew dependencies` to get a list of dependencies that we are bringing in. In some cases there would be a clash and an older version would take precedence. In these instances we have tried to exclude some of the older dependencies and directly import the later version. However, Kafka-clients would not work correctly and so we had to downgrade to v2.5.1, in particular given the following error message:
```
java.lang.NoSuchMethodError: 'org.apache.kafka.common.requests.MetadataResponse org.apache.kafka.common.requests.MetadataResponse.prepareResponse(int, java.util.Collection, java.lang.String, int, java.util.List, int)'
```

### Testing
#### User Topology Tests
* User Topology test uses a [TopologyTestDriver](https://kafka.apache.org/24/javadoc/org/apache/kafka/streams/TopologyTestDriver.html).
* The test driver creates our topologu. We specify a test topic that we can "pipe" messages to, as well as a state store to read values from.

#### Kafka Producer Tests
* Kafka Producer tests use Spring [Embedded Kafka annotation](https://docs.spring.io/spring-kafka/api/org/springframework/kafka/test/context/EmbeddedKafka.html) to run a lightweight version of kafka for testing.
* Our tests largely follow those from [Springs example](https://docs.spring.io/spring-kafka/reference/html/#embedded-kafka-annotation). We need to provide an [Embedded Kafka Broker](https://docs.spring.io/spring-kafka/api/org/springframework/kafka/test/EmbeddedKafkaBroker.html) to [Kafka Test Utils](https://docs.spring.io/spring-kafka/api/org/springframework/kafka/test/utils/KafkaTestUtils.html).
* From there we can set up a Consumer  with some Deserialisation tools for storing messages and then tell our Embedded Kafka Broker to read from a specified topic.
* Now we can strike messages to our kafka broker.
* When retrieving messages using Kafka Test Utils, we can't just send multiple messages and use the `getRecords()` function without any parameters. Otherwise this will just pass us back the first message that was sent to kafka rather than all. To avoid this issue, we pass in a `minRecords` parameter and a timeout to try to ensure that we are getting all records back.
