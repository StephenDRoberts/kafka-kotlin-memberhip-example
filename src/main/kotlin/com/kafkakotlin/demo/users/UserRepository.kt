package com.kafkakotlin.demo.users

import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class UserRepository(
        private val kafkaTemplate: KafkaTemplate<Long, Any>
) {

    fun createUser(user:User): ResponseEntity<Any>{
        return ResponseEntity.ok("200")
    }
}