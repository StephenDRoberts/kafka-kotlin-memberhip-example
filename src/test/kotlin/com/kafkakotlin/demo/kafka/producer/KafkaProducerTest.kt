package com.kafkakotlin.demo.kafka.producer

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kafkakotlin.demo.users.User
import com.kafkakotlin.demo.users.UserSerde
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = ["user-topic"], bootstrapServersProperty = "spring.kafka.bootstrap-servers", brokerProperties = ["listeners=PLAINTEXT://localhost:9093", "port=9093"])
internal class KafkaProducerTest() {
    private val objectMapper = jacksonObjectMapper()

    @Autowired
    private lateinit var underTest: KafkaProducer

    @Autowired
    private lateinit var embeddedKafkaBroker: EmbeddedKafkaBroker

    private lateinit var consumer: Consumer<String, User>

    @BeforeEach
    fun setup() {
        val userSerde = UserSerde(objectMapper).userSerde
        val consumerProps = KafkaTestUtils.consumerProps("user-group", "true", embeddedKafkaBroker)
        val consumerFactory: ConsumerFactory<String, User> = DefaultKafkaConsumerFactory<String, User>(consumerProps, StringDeserializer(), userSerde.deserializer())
        consumer = consumerFactory.createConsumer()
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "user-topic")
    }

    @Test
    fun `should put a message to the user-topic`() {
        val testUser1 = User("steve", "steve@example.com", "super-strong-password-1")
        val testUser2 = User("someone else", "someoneElse@example.com", "super-strong-password-2")

        underTest.publishMessageToKafka(testUser1)
        underTest.publishMessageToKafka(testUser2)

        val replies = KafkaTestUtils.getRecords<String, User>(consumer, 100, 2)
        assertThat(replies.count()).isGreaterThanOrEqualTo(2)
    }
}
