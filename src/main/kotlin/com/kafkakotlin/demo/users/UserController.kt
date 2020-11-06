package com.kafkakotlin.demo.users

import org.apache.kafka.streams.state.KeyValueIterator
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
        val userService: UserService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun createUser(@RequestBody user: User): Unit {
        return userService.createUser(user)
    }

    @GetMapping("/all")
    fun getAllUsers() : Map<String, User> {
        return userService.getUsers()
    }
}
