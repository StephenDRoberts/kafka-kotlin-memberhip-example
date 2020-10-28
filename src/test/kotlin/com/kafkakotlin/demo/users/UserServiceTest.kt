package com.kafkakotlin.demo.users

import com.kafkakotlin.demo.kafka.producer.KafkaProducer
import io.mockk.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity

@DisplayName("User Service Tests")
internal class UserServiceTest {
    private val userRepository = mockk<UserRepository>()
    private val kafkaProducer = mockk<KafkaProducer>()
    private val underTest = UserRepository(kafkaProducer)

    private val dummyUser = User(
            username = "Steve",
            email = "steve@steve.com",
            password = "super-strong-password"
    )

    @Test
    fun `should call userRespository with the correct parameters`() {
        val userSlot = slot<User>()
        every {userRepository.createUser(capture(userSlot))} just runs

        userRepository.createUser(dummyUser)

        assertEquals(dummyUser, userSlot.captured)
    }

}