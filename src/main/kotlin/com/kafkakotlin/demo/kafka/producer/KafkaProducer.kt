package com.kafkakotlin.demo.kafka.producer

import com.kafkakotlin.demo.users.User
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaProducer(
        private val kafkaTemplate: KafkaTemplate<String, String>
) {

    fun strikeMessageToKafka(payload: User) {
        println("striking the following payload to kafka:")
        println(payload)
        kafkaTemplate.send("users-topic", "1",payload.toString())
  }
}