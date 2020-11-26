package com.kafkakotlin.demo.users

import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

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

    @GetMapping("/{username}")
    fun getByUsername(@PathVariable username: String): Map<String, User?> {
        return userService.getByUsername(username)
    }

    @GetMapping("/remote")
    fun getProxiedLocalUsers(): Map<String, User> {
        return userService.getProxiedLocalUsers()
    }

    companion object : KLogging()
}
