package io.timemates.backend.repositories

import io.timemates.backend.types.value.Count
import io.timemates.backend.types.value.UnixTime
import kotlinx.coroutines.flow.Flow

interface UpdatesRepository {
    /**
     * Gets updates after specific update with given id. Limited to given [count] to avoid
     * big-memory consumption.
     */
    suspend fun getUpdates(
        afterUpdateId: UpdateId,
        forUserId: UsersRepository.UserId,
        count: Count
    ): Flow<List<IdentifiedUpdate>>

    /**
     * Sends update to user.
     * @param userId user that will receive update.
     * @param update specific update to be sent.
     */
    suspend fun sendUpdate(userId: UsersRepository.UserId, update: Update)

    @JvmInline
    value class UpdateId(val long: Long)

    class IdentifiedUpdate(val id: UpdateId, val update: Update)

    /**
     * Represents updates of certain user.
     */
    sealed class Update {
        /**
         * Marks when update looses its actuality.
         *
         * Used for confirmation updates and so on.
         */
        abstract val expiresAt: UnixTime?

        class SettingsUpdate(
            val timerId: TimersRepository.TimerId,
            val newSettings: TimersRepository.NewSettings
        ) : Update() {
            override val expiresAt: UnixTime? = null
        }

        class AddedTimer(
            val timerId: TimersRepository.TimerId,
            override val expiresAt: UnixTime?
        ) : Update()

        class RemovedTimer(
            val timerId: TimersRepository.TimerId,
            override val expiresAt: UnixTime?
        ) : Update()

        class TimerStarted(
            val timerId: TimersRepository.TimerId,
            override val expiresAt: UnixTime
        ) : Update()

        class TimerPaused(
            val timerId: TimersRepository.TimerId,
            override val expiresAt: UnixTime?
        ) : Update()

        class TimerAttendanceConfirmation(
            val timerId: TimersRepository.TimerId,
            override val expiresAt: UnixTime
        ) : Update()

        class TimerNoteRequired(
            val timerId: TimersRepository.TimerId,
            override val expiresAt: UnixTime?
        ) : Update()

        class TimerSessionJoined(
            val timerId: TimersRepository.TimerId,
            val memberId: UsersRepository.UserId,
            override val expiresAt: UnixTime?
        ) : Update()

        class TimerSessionLeft(
            val timerId: TimersRepository.TimerId,
            val memberId: UsersRepository.UserId,
            override val expiresAt: UnixTime?
        ) : Update()
    }
}