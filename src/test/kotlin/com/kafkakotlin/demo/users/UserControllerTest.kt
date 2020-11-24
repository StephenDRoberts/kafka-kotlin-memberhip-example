package com.kafkakotlin.demo.users

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("User Controller Tests")
internal class UserControllerTest {
    private val userService = mockk<UserService>()
    private val underTest = UserController(userService = userService)

    private val dummyUser = User(
        username = "Steve",
        email = "steve@steve.com",
        password = "super-strong-password"
    )

    @Test
    fun `should call the UserService with the correct parameters`() {
        every { userService.createUser(any()) } just runs

        underTest.createUser(dummyUser)

        verify { userService.createUser(dummyUser) }
    }
}
