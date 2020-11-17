//package com.kafkakotlin.demo.kafka
//
//import mu.KotlinLogging
//import org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG
//import org.apache.kafka.clients.consumer.ConsumerConfig
//import org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG
//import org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG
//import org.apache.kafka.clients.producer.ProducerConfig
//import org.apache.kafka.common.serialization.ByteArrayDeserializer
//import org.apache.kafka.common.serialization.StringDeserializer
//import org.apache.kafka.common.serialization.StringSerializer
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.kafka.annotation.EnableKafka
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
//import org.springframework.kafka.config.KafkaListenerContainerFactory
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory
//import org.springframework.kafka.core.DefaultKafkaProducerFactory
//import org.springframework.kafka.core.KafkaTemplate
//import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
//
//private val logger = KotlinLogging.logger { }
//
//@Configuration
//@EnableKafka
//class KafkaConfiguration(
//        @Value("\${kafka.bootstrap-servers}")
//        private val bootstrapServers: String
//) {
//
//    private val sharedProperties: Map<String, Any> = mapOf(
//            "request.timeout.ms" to "10000"
//    )
//
//    private val consumerProperties: Map<String, Any> = mapOf(
//            // list of host:port pairs used for establishing the initial connections to the Kafka cluster
//            BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
//            // allows a pool of processes to divide the work of consuming and processing records
//            GROUP_ID_CONFIG to "gstp-sportsbook-cms-int",
//            // automatically reset the offset to the earliest offset
//            AUTO_OFFSET_RESET_CONFIG to "latest",
//
//
//            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
//            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java
//    ) + sharedProperties
//
//    private val producerProperties: Map<String, Any> = mapOf(
//            // list of host:port pairs used for establishing the initial connections to the Kakfa cluster
//            BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
//            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
//            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java
//    ) + sharedProperties
//
//    @Bean
//    fun kafkaListenerContainerFactory(): KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Any>> {
//        val factory =
//                ConcurrentKafkaListenerContainerFactory<String, Any>()
//        logger.debug("Constructing Kafka Listener with properties $consumerProperties")
//        factory.consumerFactory = DefaultKafkaConsumerFactory(consumerProperties)
//        return factory
//    }
//
//    @Bean
//    fun kafkaTemplateTest(): KafkaTemplate<String, Any> {
//        val properties = producerProperties
//        return KafkaTemplate(
//                DefaultKafkaProducerFactory(
//                        properties
//                )
//        )
//    }
//}