package com.kafkakotlin.demo.kafka.producer

import org.apache.catalina.User
import org.springframework.kafka.core.KafkaTemplate

internal class KafkaProducer(
        private val kafkaTemplate: KafkaTemplate<Long, Any>
) {

    fun strikeMessageToKafka(payload: User) {
        kafkaTemplate.send("users", payload)
  }
}