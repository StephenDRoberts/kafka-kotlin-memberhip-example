package com.kafkakotlin.demo.users

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity

@DisplayName("User Service Tests")
internal class UserServiceTest {
    private val userRepository = mockk<UserRepository>()
    private val underTest = UserRepository()

    private val dummyUser = User(
            username = "Steve",
            email = "steve@steve.com",
            password = "super-strong-password"
    )

    @Test
    fun `should call userRespository with the correct parameters`() {
        val userSlot = slot<User>()
        every {userRepository.createUser(capture(userSlot))} returns ResponseEntity.ok("200")

        userRepository.createUser(dummyUser)

        assertEquals(dummyUser, userSlot.captured)
    }

}