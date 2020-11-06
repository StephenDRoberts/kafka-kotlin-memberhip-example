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

    fun getUsers(): Map<String, User> {
        val metadata = streamsBuilderFactoryBean.kafkaStreams.allMetadataForStore("user-table")
        val activeHost = metadata.first().host()
        val activePort = metadata.first().port()

        val isThisHost = activeHost == thisHost && activePort == thisPort.toInt()
        logger.info { "ActiveHost: ${activeHost}, activePort: ${activePort}" }
        logger.info { "ThisHost? $isThisHost" }


        return if(isThisHost){
            val items: KeyValueIterator<String, User> = store.getStore().all()

            logger.info { "Local: ${items.javaClass.name}" }
            val bigDump = convertKeyValuesToMap(items)
            logger.info { bigDump }
            bigDump
        } else {
            logger.info { RemoteAddress(activeHost, activePort) }

            val returnType = object: ParameterizedTypeReference<Map<String, User>>() {}
            val remoteItems = restTemplate.exchange("http://$activeHost:$activePort/user/all",
                    HttpMethod.GET,
            null,
                    returnType
            ).body

            logger.info { "Remote: ${remoteItems?.javaClass?.name}" }
            logger.info { "Remote Items: $remoteItems" }
            remoteItems ?: emptyMap()
        }
    }

    private fun convertKeyValuesToMap(items: KeyValueIterator<String, User>): Map<String, User> {
        val localItemsMap = mutableMapOf<String, User>()

        while (items.hasNext()) {
            val keyValuePair = items.next()
            localItemsMap[keyValuePair.key] = keyValuePair.value
        }
        return localItemsMap
    }

    companion object: KLogging();
}