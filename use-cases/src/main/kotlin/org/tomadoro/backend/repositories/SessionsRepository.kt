package org.tomadoro.backend.repositories

import kotlinx.coroutines.flow.Flow
import org.tomadoro.backend.domain.Count
import org.tomadoro.backend.domain.DateTime
import org.tomadoro.backend.usecases.timers.types.DetailedTimer

interface SessionsRepository {
    /**
     * Adds member to session.
     * Method is synchronized.
     */
    suspend fun addMember(
        timerId: TimersRepository.TimerId,
        userId: UsersRepository.UserId
    )

    /**
     * Removes member from timer with [timerId].
     * If it's only member of session, session will be removed.
     * Method is synchronized.
     */
    suspend fun removeMember(
        timerId: TimersRepository.TimerId,
        userId: UsersRepository.UserId
    )

    suspend fun getMembers(
        timerId: TimersRepository.TimerId,
        afterUserId: UsersRepository.UserId?,
        count: Count
    ): List<UsersRepository.UserId>

    suspend fun updatesOf(
        timerId: TimersRepository.TimerId
    ): Flow<Update>

    suspend fun getActive(
        ids: List<TimersRepository.TimerId>
    ): Map<TimersRepository.TimerId, DetailedTimer.Active.SessionInfo>

    suspend fun createConfirmation(timerId: TimersRepository.TimerId)
    suspend fun isConfirmationAvailable(timerId: TimersRepository.TimerId): Boolean
    suspend fun confirm(
        timerId: TimersRepository.TimerId,
        userId: UsersRepository.UserId
    ): Boolean

    suspend fun sendUpdate(
        timerId: TimersRepository.TimerId,
        update: Update
    )

    sealed interface Update {
        object Confirmation : Update

        @JvmInline
        value class Settings(
            val newSettings: TimersRepository.NewSettings
        ) : Update

        @JvmInline
        value class TimerStarted(val endsAt: DateTime) : Update

        @JvmInline
        value class TimerStopped(val startsAt: DateTime?) : Update

        @JvmInline
        value class UserHasJoined(val user: UsersRepository.User) : Update

        @JvmInline
        value class UserHasLeft(val user: UsersRepository.User) : Update

        @JvmInline
        value class NewNote(val note: NotesRepository.Note) : Update

        sealed interface SessionFinished : Update {
            /**
             * Marks that client has incompatible version for
             * enabled features.
             */
            object ClientIsTooOld : SessionFinished

            /**
             * Marks that request is missing some fields, or
             * they're invalid.
             */
            object BadRequest : SessionFinished
        }
    }
}

suspend fun SessionsRepository.count(timerId: TimersRepository.TimerId): Int {
    return getMembers(timerId, null, Count.MAX).count()
}