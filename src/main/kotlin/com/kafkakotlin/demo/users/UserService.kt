package com.kafkakotlin.demo.users

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class UserService {

    fun createUser(user: User): ResponseEntity<Any> {
        return ResponseEntity.ok("All good")
    }

}