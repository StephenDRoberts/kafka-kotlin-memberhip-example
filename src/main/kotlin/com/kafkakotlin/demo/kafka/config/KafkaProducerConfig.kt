package com.kafkakotlin.demo.kafka.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "kafka.producer")
data class KafkaProducerConfig(var topic: String = "", var brokers: String = "")
