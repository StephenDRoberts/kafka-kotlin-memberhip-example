package com.kafkakotlin.demo.users

import com.kafkakotlin.demo.kafka.statestorequery.StateStoreQuery
import com.kafkakotlin.demo.kafka.producer.KafkaProducer
import mu.KLogging
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.state.KeyValueIterator
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.kafka.config.StreamsBuilderFactoryBean
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class UserRepository(
    private val kafkaProducer: KafkaProducer,
    private val store: StateStoreQuery,
    private val streamsBuilderFactoryBean: StreamsBuilderFactoryBean,
    private val restTemplate: RestTemplate,
    kafkaProperties: KafkaProperties

) {
    private val applicationServer = kafkaProperties.streams.properties[StreamsConfig.APPLICATION_SERVER_CONFIG]?.split(":")
        ?: throw Exception("Unable to find spring.kafka.properties.application.server property")
    private val thisHost = applicationServer[0]
    private val thisPort = applicationServer[1]

    fun createUser(user: User) {
        return kafkaProducer.strikeMessageToKafka(user)
    }

    fun getUsers(): Map<String, User> {
        val metadata = streamsBuilderFactoryBean.kafkaStreams.allMetadataForStore("user-store")
        val hostAndPortList = metadata.map { data -> mapOf("host" to data.host(), "port" to data.port().toString()) }

        val userList = mutableMapOf<String, User>()

        val localUsers = getLocalUsers()
        for (user in localUsers) {
            userList[user.key] = user.value
        }

        val filteredHosts = hostAndPortList.filterNot { it -> it["host"] == thisHost && it["port"] == thisPort }

        for (entry in filteredHosts) {
            val remoteUsers = getRemoteUsers(entry["host"], entry["port"])
            remoteUsers.map { (key, value) -> userList[key] = value }
        }
        return userList
    }

    fun getLocalUsers(): Map<String, User> {
        return convertKeyValuesToMap(store.getStore().all())
    }

    fun getRemoteUsers(host: String?, port: String?): Map<String, User> {
        val returnType = object : ParameterizedTypeReference<Map<String, User>>() {}
        val remoteItems = restTemplate.exchange(
            "http://$host:$port/user/remote",
            HttpMethod.GET,
            null,
            returnType
        ).body

        return remoteItems ?: emptyMap()
    }

    private fun convertKeyValuesToMap(items: KeyValueIterator<String, User>): Map<String, User> {
        val localItemsMap = mutableMapOf<String, User>()

        while (items.hasNext()) {
            val keyValuePair = items.next()
            localItemsMap[keyValuePair.key] = keyValuePair.value
        }
        return localItemsMap
    }

    companion object : KLogging()
}
