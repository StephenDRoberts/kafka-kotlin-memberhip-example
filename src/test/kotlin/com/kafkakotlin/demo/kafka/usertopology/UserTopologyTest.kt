package com.kafkakotlin.demo.kafka.usertopology

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.common.hash.Hashing
import com.kafkakotlin.demo.users.User
import com.kafkakotlin.demo.users.UserSerde
import java.nio.charset.StandardCharsets
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.TestInputTopic
import org.apache.kafka.streams.TopologyTestDriver
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class UserTopologyTest() {
    private lateinit var testDriver: TopologyTestDriver
    private lateinit var userTopic: TestInputTopic<String, User>
    private lateinit var userStore: ReadOnlyKeyValueStore<String, User>

    @BeforeEach
    fun setup() {
        val config = mapOf(
            StreamsConfig.APPLICATION_ID_CONFIG to "user-service",
            StreamsConfig.BOOTSTRAP_SERVERS_CONFIG to "mock:1234",
            StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG to Serdes.String().javaClass.name,
            StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG to Serdes.String().javaClass.name
        ).toProperties()

        val builder = StreamsBuilder()
        val objectMapper = jacksonObjectMapper()
        val userTopology = UserTopology(builder, objectMapper)

        testDriver = TopologyTestDriver(builder.build(), config)
        val stringSerde = Serdes.String()
        val userSerde = UserSerde(objectMapper)
        userTopic = testDriver.createInputTopic("user-topic", stringSerde.serializer(), userSerde.userSerde.serializer())
        userStore = testDriver.getKeyValueStore<String, User>("user-store")
    }

    @AfterEach
    fun cleanup() {
        testDriver.close()
    }

    @Test
    fun `should put a message to the state store`() {
        val password = "super-strong-password-1"
        val hashedPassword = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString()
        val testUser = User("steve", "steve@example.com", password)
        val testUserWithHashedPassword = User("steve", "steve@example.com", hashedPassword)

        userTopic.pipeInput("1", testUser)

        val storedValue = userStore.get("1")

        assertThat(userStore.approximateNumEntries()).isEqualTo(1)
        assertThat(storedValue).isEqualTo(testUserWithHashedPassword)
    }
}
