package com.kafkakotlin.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KafkaKotlinUserExampleApplication

fun main(args: Array<String>) {
	runApplication<KafkaKotlinUserExampleApplication>(*args)
}
