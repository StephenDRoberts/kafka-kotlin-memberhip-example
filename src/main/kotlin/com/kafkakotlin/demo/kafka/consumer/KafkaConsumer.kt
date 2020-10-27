package com.kafkakotlin.demo.kafka.consumer

import mu.KLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaConsumer {

    @KafkaListener(topics = ["users"], groupId = "kafka-kotlin-membership")
    fun readMessage(user: String){
        logger.info { "reading message: ${user}" }
    }

    companion object : KLogging()
}