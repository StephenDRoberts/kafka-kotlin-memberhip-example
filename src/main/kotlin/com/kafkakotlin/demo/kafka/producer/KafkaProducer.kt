package com.kafkakotlin.demo.kafka.producer

import com.kafkakotlin.demo.users.User
import org.apache.kafka.common.protocol.types.Field
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.*

@Component
class KafkaProducer(
        private val kafkaTemplate: KafkaTemplate<String, User>
) {

    fun strikeMessageToKafka(payload: User) {
        val key = UUID.randomUUID().toString()
        kafkaTemplate.send("user-topic", key, payload)
  }
}