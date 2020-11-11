package com.kafkakotlin.demo.users

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.common.serialization.Serdes

class UserSerde(
    private val mapper: ObjectMapper
) {
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
}
