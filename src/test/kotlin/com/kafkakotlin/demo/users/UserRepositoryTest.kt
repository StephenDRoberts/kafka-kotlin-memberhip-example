package com.kafkakotlin.demo.users

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kafkakotlin.demo.kafka.`state-store-query`.StateStoreQuery
import com.kafkakotlin.demo.kafka.producer.KafkaProducer
import io.mockk.every
import io.mockk.mockk
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.*
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.apache.kafka.streams.state.StoreBuilder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.kafka.config.StreamsBuilderFactoryBean
import org.springframework.web.client.RestTemplate

internal class UserRepositoryTest () {
    private lateinit var testDriver: TopologyTestDriver
    private lateinit var userTopic: TestInputTopic<String, User>
    private val objectMapper = jacksonObjectMapper()
    private val kafkaProducer: KafkaProducer = mockk()
    private val stateStoreQuery: StateStoreQuery = mockk()
    private val streamsBuilderFactoryBean: StreamsBuilderFactoryBean = mockk()
    private val restTemplate: RestTemplate = mockk()
    private val kafkaProperties: KafkaProperties = mockk()

    @BeforeEach
    fun setup() {
        every { stateStoreQuery.getStore() } returns mockk<ReadOnlyKeyValueStore<String, User>>()
        val builder = StreamsBuilder()

        val config = mapOf(
                StreamsConfig.APPLICATION_ID_CONFIG to "user-service",
                StreamsConfig.BOOTSTRAP_SERVERS_CONFIG to "mock:1234",
                StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG to Serdes.String().javaClass.name,
                StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG to Serdes.String().javaClass.name
        ).toProperties()
        val topology = builder.build()
        testDriver = TopologyTestDriver(topology, config)

        val stringSerde = Serdes.String()
        val userSerde = UserSerde(objectMapper)
        builder.stream<String, User>("user-topic", Consumed.with(stringSerde, userSerde.userSerde))
        userTopic = testDriver.createInputTopic("user-topic", stringSerde.serializer(), userSerde.userSerde.serializer())
    }

    @AfterEach
    fun afterEach() {
        testDriver.close()
    }

    @Test
    fun `should add a new user to the state store`() {
        val testUser = User("steve", "steve@example.com", "super-strong-password-1")
        val userStore = testDriver.getKeyValueStore<String, User>("user-store")
        userTopic.pipeInput("123", testUser)
        println(userStore)
        println(userStore.approximateNumEntries())
        val userRepository = UserRepository(kafkaProducer, stateStoreQuery, streamsBuilderFactoryBean, restTemplate, kafkaProperties)
        val startingUserNumber = userStore.approximateNumEntries()

//        assert(startingUserNumber==null)



        assert(userStore.approximateNumEntries().toInt()==1)

    }

}