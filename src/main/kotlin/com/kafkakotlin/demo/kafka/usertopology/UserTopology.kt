package com.kafkakotlin.demo.kafka.usertopology

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.common.hash.Hashing
import com.kafkakotlin.demo.users.User
import java.nio.charset.StandardCharsets
import mu.KLogging
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.state.Stores
import org.springframework.stereotype.Component

@Component
class UserTopology(
    private val builder: StreamsBuilder,
    private val mapper: ObjectMapper
) {
    init {
        val stringSerde = Serdes.String()
        val userSerde = Serdes.serdeFrom(
            {
                _, obj: User ->
                mapper.writeValueAsBytes(obj)
            },
            {
                _, bytes ->
                mapper.readValue<User>(bytes)
            }
        )

        builder.stream("user-topic", Consumed.with(stringSerde, userSerde))
            .map { key, value ->
                val username = value.username
                val email = value.email
                val hashedPassword = Hashing.sha256().hashString(value.password, StandardCharsets.UTF_8).toString()

                KeyValue(key, User(username, email, hashedPassword))
            }
            .toTable(
                Materialized.`as`<String, User>(Stores.inMemoryKeyValueStore("user-store"))
                    .withKeySerde(stringSerde)
                    .withValueSerde(userSerde)
            )
    }
    companion object : KLogging()
}
