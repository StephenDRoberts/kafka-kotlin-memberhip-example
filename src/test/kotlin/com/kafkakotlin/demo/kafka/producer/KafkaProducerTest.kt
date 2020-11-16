package com.kafkakotlin.demo.kafka.producer

import com.kafkakotlin.demo.users.User
import kafka.Kafka
import kafka.KafkaTest
import kafka.tools.ConsoleConsumer
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.junit4.SpringRunner

//@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = ["user-topic"], bootstrapServersProperty = "spring.kafka.bootstrap-servers")
//@RunWith(SpringRunner.class)
internal class KafkaProducerTest() {

    private lateinit var underTest: KafkaProducer
//    private val kafkaTemplate = KafkaTemplate<String, User>()
    private lateinit var testUtils: KafkaTestUtils
    private lateinit var embeddedKafkaBroker: EmbeddedKafkaBroker

    @Test
    fun `should put a message to the state store`() {
        val testUser = User("steve", "steve@example.com", "super-strong-password-1")
        val consumerProps = KafkaTestUtils.consumerProps("user-group", "true", embeddedKafkaBroker)

        val consumerFactory: ConsumerFactory<Int?, String?> = DefaultKafkaConsumerFactory<Int, String>(consumerProps)
        val consumer: Consumer<Int?, String?> = consumerFactory.createConsumer()

        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "user-topic")
        underTest.strikeMessageToKafka(testUser)
        val replies = KafkaTestUtils.getRecords<Int?, String?>(consumer)
        assertThat(replies.count()).isGreaterThanOrEqualTo(1)

    }


}