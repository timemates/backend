package io.timemates.backend.repositories

import io.timemates.backend.types.value.Count
import io.timemates.backend.repositories.datasources.KafkaUpdatesDataSource
import io.timemates.backend.repositories.datasources.types.external
import io.timemates.backend.repositories.datasources.types.internal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class KafkaUpdatesRepository(private val kafkaUpdatesDataSource: KafkaUpdatesDataSource) : UpdatesRepository {
    override suspend fun getUpdates(
        afterUpdateId: UpdatesRepository.UpdateId,
        forUserId: UsersRepository.UserId,
        count: Count
    ): Flow<List<UpdatesRepository.IdentifiedUpdate>> {
        return kafkaUpdatesDataSource.retrieveUpdates(forUserId.int, afterUpdateId.long)
            .map { records ->
                records.map {
                    UpdatesRepository.IdentifiedUpdate(
                        UpdatesRepository.UpdateId(it.offset()), it.value().external()
                    )
                }
            }
    }

    override suspend fun sendUpdate(userId: UsersRepository.UserId, update: UpdatesRepository.Update) {
        kafkaUpdatesDataSource.sendUpdate(userId.int, update.internal())
    }

}