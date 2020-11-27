package com.kafkakotlin.demo.kafka.statestorequery

import com.kafkakotlin.demo.users.User
import io.mockk.every
import io.mockk.mockk
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StoreQueryParameters
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.kafka.config.StreamsBuilderFactoryBean

internal class StateStoreQueryTest {
    private val streamsBuilderFactoryBean = mockk<StreamsBuilderFactoryBean>()
    private val kafkaStreams = mockk<KafkaStreams>()
    private val store = mockk<ReadOnlyKeyValueStore<String, User>>()
    private val stateStoreQuery = StateStoreQuery(streamsBuilderFactoryBean)

    @Test
    fun `should return a state store`() {
        every { streamsBuilderFactoryBean.kafkaStreams } returns kafkaStreams
        every { kafkaStreams.store(any<StoreQueryParameters<*>>()) } returns store

        val stateStore = stateStoreQuery.getStore()

        assertThat(stateStore).isEqualTo(store)
    }
}
