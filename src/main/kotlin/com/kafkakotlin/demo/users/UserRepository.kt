package com.kafkakotlin.demo.users

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class UserRepository {

    fun createUser(user:User): ResponseEntity<Any>{
        return ResponseEntity.ok("200")
    }
}