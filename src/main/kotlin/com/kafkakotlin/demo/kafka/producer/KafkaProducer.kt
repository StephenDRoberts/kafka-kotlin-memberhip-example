package com.kafkakotlin.demo.kafka.producer

import com.kafkakotlin.demo.users.User
import java.util.*
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, User>
) {

    fun strikeMessageToKafka(payload: User) {
        val key = UUID.randomUUID().toString()
        kafkaTemplate.send("user-topic", key, payload)
    }
}
