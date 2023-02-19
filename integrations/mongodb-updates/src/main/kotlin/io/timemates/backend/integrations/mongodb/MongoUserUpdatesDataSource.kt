package io.timemates.backend.integrations.mongodb

import com.mongodb.reactivestreams.client.MongoClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.*
import org.litote.kmongo.id.StringId
import org.litote.kmongo.reactivestreams.getCollectionOfName

class MongoUserUpdatesDataSource(
    private val kmongo: MongoClient
) {

    private val database = kmongo.getDatabase("user_updates")

    fun getUpdates(
        userId: Long,
        lastReceivedId: String,
        countPerPage: Int,
        currentTime: Long
    ): Flow<Update> {
        return database.getCollectionOfName<Update>(userId.toString())
            .find()
            .filter(
                and(
                    Update::updateId gt StringId(lastReceivedId),
                    Update::expiresAt gt currentTime
                )
            )
            .batchSize(countPerPage)
            .asFlow()
    }

    suspend fun getLastUpdateId(userId: Long): String {
        return database.getCollectionOfName<Update>(userId.toString())
            .find()
            .sort(descending(Update::updateId))
            .limit(1)
            .awaitLast()
            .updateId.toString()
    }

    suspend fun addUpdate(
        userId: Long,
        update: Update
    ) {
        database.getCollectionOfName<Update>(userId.toString())
            .insertOne(update)
            .awaitFirst()
    }

    /**
     * Represents updates of certain user.
     */
    @Serializable
    sealed class Update {
        /**
         * Marks when update looses its actuality.
         *
         * Used for confirmation updates and so on.
         */
        abstract val expiresAt: Long?

        @Contextual
        @SerialName("_id")
        abstract val updateId: Id<Update>

        @Serializable
        class SettingsUpdate(
            override val updateId: Id<Update> = newId(),
            val timerId: Int,
            val newSettings: NewSettings
        ) : Update() {
            override val expiresAt: Long? = null
        }

        @Serializable
        class AddedTimer(
            override val updateId: Id<Update> = newId(),
            val timerId: Int,
            override val expiresAt: Long?
        ) : Update()

        @Serializable
        class RemovedTimer(
            override val updateId: Id<Update> = newId(),
            val timerId: Int,
            override val expiresAt: Long?
        ) : Update()

        @Serializable
        class TimerStarted(
            override val updateId: Id<Update> = newId(),
            val timerId: Int,
            override val expiresAt: Long
        ) : Update()

        @Serializable
        class TimerPaused(
            override val updateId: Id<Update> = newId(),
            val timerId: Int,
            override val expiresAt: Long?
        ) : Update()

        @Serializable
        class TimerAttendanceConfirmation(
            override val updateId: Id<Update> = newId(),
            val timerId: Int,
            override val expiresAt: Long
        ) : Update()

        @Serializable
        class TimerNoteRequired(
            override val updateId: Id<Update> = newId(),
            val timerId: Int,
            override val expiresAt: Long?
        ) : Update()

        @Serializable
        class TimerSessionJoined(
            override val updateId: Id<Update> = newId(),
            val timerId: Int,
            val memberId: Long,
            override val expiresAt: Long?
        ) : Update()

        @Serializable
        class TimerSessionLeft(
            override val updateId: Id<Update> = newId(),
            val timerId: Int,
            val memberId: Int,
            override val expiresAt: Long?
        ) : Update()
    }

    @Serializable
    class NewSettings(
        val workTime: Long? = null,
        val restTime: Long? = null,
        val bigRestTime: Long? = null,
        val bigRestEnabled: Boolean? = null,
        val bigRestPer: Int? = null,
        val isEveryoneCanPause: Boolean? = null,
        val isConfirmationRequired: Boolean? = null,
        val isNotesEnabled: Boolean? = null
    )
}