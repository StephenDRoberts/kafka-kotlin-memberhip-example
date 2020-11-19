package com.kafkakotlin.demo.kafka.producer

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kafkakotlin.demo.users.User
import com.kafkakotlin.demo.users.UserSerde
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.StringDeserializer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.*
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

//@SpringBootTest
//@EmbeddedKafka(partitions = 1, topics = ["user-topic"], bootstrapServersProperty = "spring.kafka.bootstrap-servers")
//@ExtendWith(SpringExtension::class)
//internal class KafkaProducerTest() {
//
//    private lateinit var underTest: KafkaProducer
////    private val kafkaTemplate = KafkaTemplate<String, User>()
//    private lateinit var testUtils: KafkaTestUtils
//    private lateinit var embeddedKafkaBroker: EmbeddedKafkaBroker
//
//    @Test
//    fun `should put a message to the state store`() {
//        val testUser = User("steve", "steve@example.com", "super-strong-password-1")
//        val consumerProps = KafkaTestUtils.consumerProps("user-group", "true", embeddedKafkaBroker)
//
//        val consumerFactory: ConsumerFactory<Int?, String?> = DefaultKafkaConsumerFactory<Int, String>(consumerProps)
//        val consumer: Consumer<Int?, String?> = consumerFactory.createConsumer()
//
//        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "user-topic")
//        underTest.strikeMessageToKafka(testUser)
//        val replies = KafkaTestUtils.getRecords<Int?, String?>(consumer)
//        assertThat(replies.count()).isGreaterThanOrEqualTo(1)
//    }
//}


@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = ["user-topic"], bootstrapServersProperty = "spring.kafka.bootstrap-servers", brokerProperties = ["listeners=PLAINTEXT://localhost:9093", "port=9093"])
@ExtendWith(SpringExtension::class)
internal class KafkaProducerTest() {
    private val objectMapper = jacksonObjectMapper()
    private val countDownLatch = CountDownLatch(5)

    @Autowired
    private lateinit var underTest: KafkaProducer


//    @Autowired
//    private val kafkaTemplate = KafkaTemplate<String, User>()
//    @Autowired
//    private lateinit var testUtils: KafkaTestUtils

    @Autowired
    private lateinit var embeddedKafkaBroker: EmbeddedKafkaBroker

    @Test
    fun `should put a message to the state store`() {
        println("****STARTING THE TEST****")
        val stringSerde = Serdes.String()
        val userSerde = UserSerde(objectMapper)
        val testUser1 = User("steve", "steve@example.com", "super-strong-password-1")
        val testUser2 = User("someone else", "someoneElse@example.com", "super-strong-password-2")
        val consumerProps = KafkaTestUtils.consumerProps("user-group", "true", embeddedKafkaBroker)
        val consumerFactory: ConsumerFactory<String?, String?> = DefaultKafkaConsumerFactory<String, String>(consumerProps, StringDeserializer(), StringDeserializer())

        val consumer: Consumer<String?, String?> = consumerFactory.createConsumer()
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "user-topic")
        println("****STRIKING MESSAGE****")
        underTest.strikeMessageToKafka(testUser1)
        underTest.strikeMessageToKafka(testUser2)
        countDownLatch.await(1000,TimeUnit.MILLISECONDS)
        val replies = KafkaTestUtils.getRecords<String?, String?>(consumer, 100, 2)
        println("****REPLIES****")
        println(replies.records(TopicPartition("user-topic", 0))[0].value())
        println(replies.records(TopicPartition("user-topic", 0))[1].value())
        println(replies.count())
        assertThat(replies.count()).isGreaterThanOrEqualTo(2)

    }
}

//@TestConfiguration
//internal class TestConfig {
//    @Autowired
//    private lateinit var embeddedKafkaBroker: EmbeddedKafkaBroker
//
//    @Bean
//    internal fun producerFactory(): ProducerFactory<String, User> {
//        return DefaultKafkaProducerFactory(KafkaTestUtils.producerProps(embeddedKafkaBroker));
//    }
//
//    @Bean
//    internal fun kafkaTemplate(): KafkaTemplate<String, User> {
//        val kafkaTemplate = KafkaTemplate(producerFactory());
//        kafkaTemplate.setDefaultTopic("user-topic");
//        return kafkaTemplate;
//    }
//}

