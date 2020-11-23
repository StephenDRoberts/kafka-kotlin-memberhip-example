package com.kafkakotlin.demo.users

import com.kafkakotlin.demo.kafka.producer.KafkaProducer
import com.kafkakotlin.demo.kafka.statestorequery.StateStoreQuery
import io.mockk.*
import org.apache.kafka.streams.KafkaStreams
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.kafka.config.StreamsBuilderFactoryBean
import org.springframework.web.client.RestTemplate

internal class UserRepositoryTest {
    private val kafkaProducer = mockk<KafkaProducer>()
    private val store = mockk<StateStoreQuery>()
    private val streamsBuilderFactoryBean = mockk<StreamsBuilderFactoryBean>()
    private val restTemplate = mockk<RestTemplate>()
    private val kafkaProperties = mockk<KafkaProperties>()

    private val kafkaStreams = mockk<KafkaStreams>()
    private val properties = mapOf("application.server" to "localhost:9999")

    private lateinit var underTest: UserRepository

    @BeforeEach
    fun setup(){
//        every { streamsBuilderFactoryBean.kafkaStreams } returns kafkaStreams
        every { kafkaProperties.streams.properties } returns properties

        underTest = UserRepository(kafkaProducer, store, streamsBuilderFactoryBean, restTemplate, kafkaProperties)
    }

    @Test
    fun `should pass given User to kafkaProducer when creating a user`(){
        val testUser = User("steve", "steve@example.com", "super-strong-password-1")
        val slot = slot<User>()

        every { kafkaProducer.strikeMessageToKafka(capture(slot)) } just runs
        underTest.createUser(testUser)

        assertThat(testUser).isEqualTo(slot.captured)
    }


}

