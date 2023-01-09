package io.timemates.backend.repositories.datasources

import io.timemates.backend.repositories.datasources.types.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serdes.VoidSerde
import org.apache.kafka.common.serialization.Serializer
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.Topology
import java.time.Duration
import java.util.*
import kotlin.coroutines.coroutineContext

class KafkaUpdatesDataSource(private val properties: Properties) {

    private val kafkaStreams = KafkaStreams(Topology(), properties)
    private val serdes = UpdateSerializer(Json)
    private val producer = KafkaProducer(properties, VoidSerde().serializer(), serdes)

    fun retrieveUpdates(
        userId: Int,
        offset: Long
    ): Flow<ConsumerRecords<Void, Update>> {
        val consumer = KafkaConsumer(properties, VoidSerde().deserializer(), serdes)
        consumer.seek(TopicPartition("updates-$userId", 0), offset)

        return flow {
            while(coroutineContext.isActive) {
                val records: ConsumerRecords<Void, Update> =
                    consumer.poll(Duration.ofMillis(1000))
                emit(records)
            }
        }.onCompletion { consumer.close() }
    }

    fun sendUpdate(userId: Int, update: Update) {
        producer.send(ProducerRecord("updates-$userId", 0, null, update))
    }

    private class UpdateSerializer(
        private val json: Json
    ) : Serializer<Update>, Deserializer<Update> {
        override fun close() {}

        override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {
        }

        override fun deserialize(topic: String?, data: ByteArray?): Update {
            return json.decodeFromString(Update.serializer(), String(data!!))
        }

        override fun serialize(topic: String?, data: Update): ByteArray {
            TODO("Not yet implemented")
        }
    }
}