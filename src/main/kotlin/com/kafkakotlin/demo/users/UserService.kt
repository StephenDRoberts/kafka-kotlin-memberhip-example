package com.kafkakotlin.demo.users

import org.apache.kafka.streams.state.KeyValueIterator
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class UserService(
        val userRepository: UserRepository
) {

    fun createUser(user: User): Unit {
        return userRepository.createUser(user)
    }

    fun getUsers(): Map<String, User> {
        return userRepository.getUsers()
    }

}