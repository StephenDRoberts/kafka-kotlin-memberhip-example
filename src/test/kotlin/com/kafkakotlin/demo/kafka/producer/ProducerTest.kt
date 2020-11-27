package com.kafkakotlin.demo.kafka.producer

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest()
@TestPropertySource(properties = ["/application-test.properties"])
@EmbeddedKafka(topics = ["user-topic"])
@ExtendWith(SpringExtension::class)
class ProducerTest {

//    TODO setup kafka producer test like with custom consumer/producer
    @Test
    fun `runs`() {
    }
}
