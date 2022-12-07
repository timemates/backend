package org.tomadoro.backend.repositories

import kotlinx.coroutines.flow.Flow
import org.tomadoro.backend.domain.DateTime

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

    suspend fun getMembers(timerId: TimersRepository.TimerId): List<UsersRepository.UserId>

    suspend fun updatesOf(
        timerId: TimersRepository.TimerId
    ): Flow<Update>

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

        object SessionFinished : Update
    }
}

suspend fun SessionsRepository.count(timerId: TimersRepository.TimerId): Int {
    return getMembers(timerId).count()
}