package com.kafkakotlin.demo.kafka.steamsprocessor

import com.kafkakotlin.demo.users.User
import mu.KLogging
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KStream
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaStreamsProcessor(
        private val kStream: KStream<Long, String>,
        private val streamsBuilder: StreamsBuilder
) {

    @KafkaListener(topics = ["users"], groupId = "kafka-kotlin-membership")
    fun processMessage(user: String){
        val userStream: KStream<Long, String> = streamsBuilder.stream("users")
        userStream.to("my-new-store")
        }



    companion object : KLogging()
}