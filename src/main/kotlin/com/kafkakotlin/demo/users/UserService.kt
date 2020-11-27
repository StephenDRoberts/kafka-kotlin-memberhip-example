package com.kafkakotlin.demo.users

import org.springframework.stereotype.Service

@Service
class UserService(
    val userRepository: UserRepository
) {

    fun createUser(user: User) = userRepository.createUser(user)

    fun getUsers(): Map<String, User> = userRepository.getUsers()

    fun getByUsername(username: String): Map<String, User>? = userRepository.getByUsername(username)

    fun getProxiedLocalUsers(): Map<String, User> = userRepository.getLocalUsers()
}
