//package com.kafkakotlin.demo.kafka.producer
//
//import com.kafkakotlin.demo.users.User
//import org.junit.jupiter.api.Test
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.kafka.test.context.EmbeddedKafka
//
////@SpringBootTest
////@EmbeddedKafka(partitions = 1, topics = ["user-topic"], bootstrapServersProperty = "spring.kafka.bootstrap-servers")
////@ExtendWith(SpringExtension::class)
////internal class KafkaProducerTest() {
////
////    private lateinit var underTest: KafkaProducer
//////    private val kafkaTemplate = KafkaTemplate<String, User>()
////    private lateinit var testUtils: KafkaTestUtils
////    private lateinit var embeddedKafkaBroker: EmbeddedKafkaBroker
////
////    @Test
////    fun `should put a message to the state store`() {
////        val testUser = User("steve", "steve@example.com", "super-strong-password-1")
////        val consumerProps = KafkaTestUtils.consumerProps("user-group", "true", embeddedKafkaBroker)
////
////        val consumerFactory: ConsumerFactory<Int?, String?> = DefaultKafkaConsumerFactory<Int, String>(consumerProps)
////        val consumer: Consumer<Int?, String?> = consumerFactory.createConsumer()
////
////        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "user-topic")
////        underTest.strikeMessageToKafka(testUser)
////        val replies = KafkaTestUtils.getRecords<Int?, String?>(consumer)
////        assertThat(replies.count()).isGreaterThanOrEqualTo(1)
////    }
////}
//
//@SpringBootTest
//@EmbeddedKafka(partitions = 1, topics = ["user-topic2"], bootstrapServersProperty = "spring.kafka.bootstrap-servers")
////@ExtendWith(SpringExtension::class)
//internal class KafkaProducerTest() {
//    @Autowired
//    private lateinit var underTest: KafkaProducer
////    @Autowired
////    private val kafkaTemplate = KafkaTemplate<String, User>()
////    @Autowired
////    private lateinit var testUtils: KafkaTestUtils
//
////    @Autowired
////    private lateinit var embeddedKafkaBroker:  EmbeddedKafkaBroker
//
//    @Test
//    fun `should put a message to the state store`() {
//        val testUser = User("steve", "steve@example.com", "super-strong-password-1")
////        val consumerProps = KafkaTestUtils.consumerProps("user-group", "true", embeddedKafkaBroker)
////        val consumerFactory: ConsumerFactory<Int?, String?> = DefaultKafkaConsumerFactory<Int, String>(consumerProps)
////        val consumer: Consumer<Int?, String?> = consumerFactory.createConsumer()
////        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "user-topic")
//        underTest.strikeMessageToKafka(testUser)
////        val replies = KafkaTestUtils.getRecords<Int?, String?>(consumer)
//
////        val replies = testUtils()
////        assertThat(replies.count()).isGreaterThanOrEqualTo(1)
//    }
//}

