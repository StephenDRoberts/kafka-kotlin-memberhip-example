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
        println("striking the following payload to kafka:")
        println(payload)
//        val key = UUID.randomUUID().toString()
        kafkaTemplate.send("users-topic", "1", payload)
  }
}