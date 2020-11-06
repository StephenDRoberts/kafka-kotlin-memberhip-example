package com.kafkakotlin.demo.restTemplate

import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate
import org.springframework.context.annotation.Configuration


@Configuration
class RestConfig {
    @Bean
    fun restTemplate(): RestTemplate = RestTemplate()
}
