package com.kafkakotlin.demo.kafka.topology

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kafkakotlin.demo.users.User
import com.kafkakotlin.demo.users.UserSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.TestInputTopic
import org.apache.kafka.streams.TopologyTestDriver
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class UserTopologyTest() {
    private lateinit var testDriver: TopologyTestDriver
    private lateinit var userTopology: UserTopology
    private lateinit var userTopic: TestInputTopic<String, User>
    private lateinit var userStore: ReadOnlyKeyValueStore<String, User>
    private val builder = StreamsBuilder()
    private val objectMapper = jacksonObjectMapper()

    @BeforeEach
    fun setup() {
        val config = mapOf(
            StreamsConfig.APPLICATION_ID_CONFIG to "user-service",
            StreamsConfig.BOOTSTRAP_SERVERS_CONFIG to "mock:1234",
            StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG to Serdes.String().javaClass.name,
            StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG to Serdes.String().javaClass.name
        ).toProperties()
        userTopology = UserTopology(builder, objectMapper)
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
        val testUser = User("steve", "steve@example.com", "super-strong-password-1")

        userTopic.pipeInput("1", testUser)

        val storedValue = userStore.get("1")

        assertEquals(userStore.approximateNumEntries(), 1)
        assertEquals(storedValue, testUser)
    }
}
