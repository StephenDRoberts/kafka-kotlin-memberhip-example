package com.kafkakotlin.demo.kafka.producer

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kafkakotlin.demo.users.User
import com.kafkakotlin.demo.users.UserSerde
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = ["user-topic"], bootstrapServersProperty = "spring.kafka.bootstrap-servers", brokerProperties = ["listeners=PLAINTEXT://localhost:9093", "port=9093"])
@ExtendWith(SpringExtension::class)
internal class KafkaProducerTest() {
    private val objectMapper = jacksonObjectMapper()

    @Autowired
    private lateinit var underTest: KafkaProducer

    @Autowired
    private lateinit var embeddedKafkaBroker: EmbeddedKafkaBroker

    @Test
    fun `should put a message to the state store`() {
        val userSerde = UserSerde(objectMapper).userSerde
        val testUser1 = User("steve", "steve@example.com", "super-strong-password-1")
        val testUser2 = User("someone else", "someoneElse@example.com", "super-strong-password-2")

        val consumerProps = KafkaTestUtils.consumerProps("user-group", "true", embeddedKafkaBroker)
        val consumerFactory: ConsumerFactory<String, User> = DefaultKafkaConsumerFactory<String, User>(consumerProps, StringDeserializer(), userSerde.deserializer())
        val consumer: Consumer<String, User> = consumerFactory.createConsumer()
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "user-topic")

        underTest.strikeMessageToKafka(testUser1)
        underTest.strikeMessageToKafka(testUser2)
        val replies = KafkaTestUtils.getRecords<String, User>(consumer, 100, 2)

        assertThat(replies.count()).isGreaterThanOrEqualTo(2)
    }
}
