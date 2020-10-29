//package com.kafkakotlin.demo.kafka.steamsprocessor
//
//import com.kafkakotlin.demo.kafka.topology.UserTopology
//import mu.KLogging
//import org.apache.kafka.common.serialization.Serdes
//import org.apache.kafka.streams.kstream.KStream
//import org.springframework.stereotype.Component
//
//@Component
//class KafkaStreamsProcessor(
//        private val topology: UserTopology
//) {
//
//    fun processMessage(user: String){
//        val userStream: KStream<Long, String> = topology.provideTopology()
//        logger.info { "putting message into store" }
//        userStream.to("my-new-store")
//        }
//
//
//
//    companion object : KLogging()
//}