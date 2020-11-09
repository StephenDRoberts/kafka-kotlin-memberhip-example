package com.kafkakotlin.demo.users

import com.kafkakotlin.demo.kafka.`state-store-query`.StateStoreQuery
import com.kafkakotlin.demo.kafka.producer.KafkaProducer
import com.kafkakotlin.demo.metadata.RemoteAddress
import mu.KLogging
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.state.KeyValueIterator
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.kafka.config.StreamsBuilderFactoryBean
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import org.springframework.web.client.getForEntity

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

    fun createUser(user:User): Unit {
        return kafkaProducer.strikeMessageToKafka(user)
    }

    fun getUsers(): List<Map<String, User>> {
        val metadata = streamsBuilderFactoryBean.kafkaStreams.allMetadataForStore("user-store")
        val hostAndPortList = metadata.map { data -> mapOf("host" to data.host(), "port" to data.port().toString()) }

        val userList = mutableListOf<Map<String, User>>()

        for(entry in hostAndPortList) {

            var activeHost = entry["host"]
            var activePort = entry["port"]

            logger.info { "Host: ${activeHost}" }
            logger.info { "Port: ${activePort}" }
            val isThisHost = activeHost == thisHost && activePort == thisPort

            if (isThisHost) {
                val items: KeyValueIterator<String, User> = store.getStore().all()
                logger.info { "Local:" }
                logger.info { items }
                val bigDump = convertKeyValuesToMap(items)
                logger.info { bigDump }

                userList.add(bigDump)
            } else {
                logger.info { "Going Remote" }

                userList.add(getRemoteUsers(activeHost, activePort).first())
            }
        }
        logger.info { "Trying to return" }
        return userList
    }

    fun getRemoteUsers(host: String?, port: String?): List<Map<String, User>>{
        val returnType = object : ParameterizedTypeReference<Map<String, User>>() {}
        val remoteItems = restTemplate.exchange("http://$host:$port/user/all",
                HttpMethod.GET,
                null,
                returnType
        ).body

        logger.info { "Remote: ${remoteItems?.javaClass?.name}" }
        logger.info { "Remote Items: $remoteItems" }
        return listOf(remoteItems ?: emptyMap())
    }

    private fun convertKeyValuesToMap(items: KeyValueIterator<String, User>): Map<String, User> {
        val localItemsMap = mutableMapOf<String, User>()

        logger.info { "HasNext? ${items.hasNext()}" }
        logger.info { items.toString() }
        while (items.hasNext()) {
            val keyValuePair = items.next()
            localItemsMap[keyValuePair.key] = keyValuePair.value
        }
        return localItemsMap
    }

    companion object: KLogging();
}