package com.kafkakotlin.demo.users

import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    val userService: UserService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun createUser(@RequestBody user: User) {
        return userService.createUser(user)
    }

    @GetMapping
    fun getAllUsers(): Map<String, User> {
        return userService.getUsers()
    }

    @GetMapping("/remote")
    fun getProxiedLocalUsers(): Map<String, User> {
        return userService.getProxiedLocalUsers()
    }

    companion object : KLogging()
}
