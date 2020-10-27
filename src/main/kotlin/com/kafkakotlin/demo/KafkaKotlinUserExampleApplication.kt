package com.kafkakotlin.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka

@SpringBootApplication
@EnableKafka
class KafkaKotlinUserExampleApplication

fun main(args: Array<String>) {
	runApplication<KafkaKotlinUserExampleApplication>(*args)
}
