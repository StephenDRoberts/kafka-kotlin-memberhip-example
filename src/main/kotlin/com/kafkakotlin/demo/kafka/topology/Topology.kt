package com.kafkakotlin.demo.kafka.topology

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kafkakotlin.demo.users.User
import mu.KLogging
import org.apache.kafka.common.serialization.Serdes
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
        private val objectMapper: ObjectMapper = jacksonObjectMapper()
) {
    init{
        val stringSerde = Serdes.String()

//        Stores.keyValueStoreBuilder(
//                Stores.inMemoryKeyValueStore("user-store"), stringSerde, stringSerde)
//        )
//
//        topology.addSource("Source", "hashed-topic")
//                .addProcessor()
        builder.stream("users-topic", Consumed.with(stringSerde, stringSerde))
                .map { key, value -> KeyValue(key, value.hashCode())}
                .toTable(Materialized.`as`("steves-bang-average-table"))



//                .groupBy { key, user -> key}

//                .toTable(Named.`as`("steves-amazing-table"))
    }
    companion object: KLogging()
}