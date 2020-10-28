package com.kafkakotlin.demo.users

import com.kafkakotlin.demo.kafka.producer.KafkaProducer
import org.springframework.stereotype.Component

@Component
class UserRepository(
        private val kafkaProducer: KafkaProducer
) {

    fun createUser(user:User): Unit {
        println("Repo")
        return kafkaProducer.strikeMessageToKafka(user)
    }
}