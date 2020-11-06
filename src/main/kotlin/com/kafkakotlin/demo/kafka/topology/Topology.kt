package com.kafkakotlin.demo.kafka.topology

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.kafkakotlin.demo.users.User
import com.kafkakotlin.demo.users.UserSerde
import mu.KLogging
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Grouped
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.Named
import org.apache.kafka.streams.state.Stores
import org.apache.kafka.streams.state.internals.KeyValueStoreBuilder
import org.springframework.kafka.support.serializer.JsonSerde
import org.springframework.stereotype.Component

@Component
class UserTopology(
        private val builder: StreamsBuilder,
        private val mapper: ObjectMapper
) {
    init{
        val stringSerde = Serdes.String()
        val userSerde = Serdes.serdeFrom({
            _, obj: User -> mapper.writeValueAsBytes(obj)
        }, {
            _, bytes -> mapper.readValue<User>(bytes)
        })

        builder.stream("user-topic", Consumed.with(stringSerde, userSerde))
                .map { key, value -> KeyValue(key, value)}
                .toTable(Materialized.`as`<String, User>(Stores.inMemoryKeyValueStore("user-store"))
                       .withKeySerde(stringSerde)
                       .withValueSerde(userSerde)
                )


    }
    companion object: KLogging()
}