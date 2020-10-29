package com.kafkakotlin.demo.kafka.topology

import mu.KLogging
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Named
import org.apache.kafka.streams.state.Stores
import org.apache.kafka.streams.state.internals.KeyValueStoreBuilder
import org.springframework.stereotype.Component

@Component
class UserTopology(
        private val builder: StreamsBuilder
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
                .toTable(Named.`as`("steves-amazing-table"))
    }
    companion object: KLogging()
}