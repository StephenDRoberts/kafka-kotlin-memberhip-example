package com.kafkakotlin.demo.users

import com.kafkakotlin.demo.kafka.producer.KafkaProducer
import com.kafkakotlin.demo.kafka.statestorequery.StateStoreQuery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.KeyQueryMetadata
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.state.HostInfo
import org.apache.kafka.streams.state.KeyValueIterator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.core.ParameterizedTypeReference
import org.springframework.kafka.config.StreamsBuilderFactoryBean
import org.springframework.web.client.RestTemplate

internal class UserRepositoryTest {
    private val kafkaProducer = mockk<KafkaProducer>()
    private val store = mockk<StateStoreQuery>()
    private val streamsBuilderFactoryBean = mockk<StreamsBuilderFactoryBean>()
    private val restTemplate = mockk<RestTemplate>()
    private val kafkaProperties = mockk<KafkaProperties>()
    private val kafkaStreams = mockk<KafkaStreams>()
//    private val keyQueryMetadata = mockk<KeyQueryMetadata>()

    private val properties = mapOf("application.server" to "localhost:9999")
    private val testUser = User("steve", "steve@example.com", "super-strong-password-1")

    private val keyValueIterator = mockk<KeyValueIterator<String, User>>()
    private lateinit var underTest: UserRepository

    @BeforeEach
    fun setup() {
        every { kafkaProperties.streams.properties } returns properties

        underTest = UserRepository(kafkaProducer, store, streamsBuilderFactoryBean, restTemplate, kafkaProperties)
    }

    @Test
    fun `should pass given User to kafkaProducer when creating a user`() {
        every { kafkaProducer.publishMessageToKafka(any()) } just runs
        underTest.createUser(testUser)

        verify { kafkaProducer.publishMessageToKafka(testUser) }
    }

    @Nested
    inner class LocalStore {
        @Test
        fun `should return a map of users from the local store`() {
            every { store.getStore().all() } returns keyValueIterator
            every { keyValueIterator.hasNext() } returns true andThen false
            every { keyValueIterator.next() } returns KeyValue("1", testUser)

            val localUsers = underTest.getLocalUsers()

            assertThat(localUsers["1"]).isEqualTo(testUser)
        }
    }

    @Nested
    inner class RemoteStore {
        @Test
        fun `should return a map of users from the remote store`() {
            val returnType = object : ParameterizedTypeReference<Map<String, User>>() {}
            every { restTemplate.exchange(any<String>(), any(), null, returnType).body } returns mapOf("1" to testUser)

            val remoteUsers = underTest.getRemoteUsers("localhost", "9099")

            assertThat(remoteUsers["1"]).isEqualTo(testUser)
        }

        @Test
        fun `should return an empty map from the remote store if no users are found`() {
            val returnType = object : ParameterizedTypeReference<Map<String, User>>() {}
            every { restTemplate.exchange(any<String>(), any(), null, returnType).body } returns emptyMap()

            val remoteUsers = underTest.getRemoteUsers("localhost", "9099")

            assertThat(remoteUsers.size).isEqualTo(0)
        }
    }

    @Nested
    inner class IndividualUsers {

        @Test
        fun `should return a map of a specific user`() {
            val hostInfo = HostInfo("localhost", 9999)
            val username = "steve"
            every { streamsBuilderFactoryBean.kafkaStreams } returns kafkaStreams
            every {streamsBuilderFactoryBean.kafkaStreams.queryMetadataForKey(any(), any(), Serdes.String().serializer())} returns KeyQueryMetadata(hostInfo, emptySet(), 1)
            every { store.getStore().get(any()) } returns testUser

            val result = underTest.getByUsername(username)

            assertThat(result?.size).isEqualTo(1)
        }
    }
}
