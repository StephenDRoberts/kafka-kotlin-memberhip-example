package com.kafkakotlin.demo.users

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class UserService(
        val userRepository: UserRepository
) {

    fun createUser(user: User): Unit {
        return userRepository.createUser(user)
    }

}