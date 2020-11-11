package com.kafkakotlin.demo.users

import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    val userService: UserService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun createUser(@RequestBody user: User) {
        return userService.createUser(user)
    }

    @GetMapping("/all")
    fun getAllUsers(): Map<String, User> {
        return userService.getUsers()
    }

    @GetMapping("/remote")
    fun getRemoteUsers(): Map<String, User> {
        return userService.getRemoteUsers()
    }

    companion object : KLogging()
}
