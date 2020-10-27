package com.kafkakotlin.demo.users

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity


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
    fun `should respond a successful response`() {
        every { userService.createUser(any())} returns ResponseEntity.ok("200")

        val response = underTest.createUser(dummyUser)

        assertTrue(response.statusCode.is2xxSuccessful)
    }

    @Test
    fun `should call the UserService with the correct parameters`() {
        val userSlot = slot<User>()
        every {userService.createUser(capture(userSlot))} returns ResponseEntity.ok("200")

        userService.createUser(dummyUser)

        assertEquals(dummyUser, userSlot.captured)
    }

}