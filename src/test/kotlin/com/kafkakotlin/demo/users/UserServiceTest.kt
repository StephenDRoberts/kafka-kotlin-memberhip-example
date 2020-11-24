package com.kafkakotlin.demo.users

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("User Service Tests")
internal class UserServiceTest {
    private val userRepository = mockk<UserRepository>()
    private val underTest = UserService(userRepository = userRepository)

    private val dummyUser = User(
        username = "Steve",
        email = "steve@steve.com",
        password = "super-strong-password"
    )

    @Test
    fun `should call userRespository with the correct parameters`() {
        every { userRepository.createUser(any()) } just runs
        underTest.createUser(dummyUser)

        verify { userRepository.createUser(dummyUser) }
    }
}
