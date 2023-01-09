package io.timemates.backend.types

import io.timemates.backend.types.value.Count
import io.timemates.backend.types.value.UnixTime
import io.timemates.backend.types.value.TimerName
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.repositories.UsersRepository

sealed class DetailedTimer(
    val timerId: TimersRepository.TimerId,
    val name: TimerName,
    val ownerId: UsersRepository.UserId,
    val settings: TimersRepository.Settings
) {
    class Active(
        timerId: TimersRepository.TimerId,
        name: TimerName,
        ownerId: UsersRepository.UserId,
        settings: TimersRepository.Settings,
        val sessionInfo: SessionInfo
    ) : DetailedTimer(timerId, name, ownerId, settings) {
        class SessionInfo(
            val participantsCount: Count,
            val phase: Phase,
            val phaseEndsAt: UnixTime?
        )

        enum class Phase {
            /**
             * Marks that timer currently paused.
             * It doesn't take in count pause by user.
             */
            PAUSE,

            /**
             * Marks that timer currently is running (at work time).
             */
            RUNNING,

            /**
             * Marks that timer is waiting to start, it happens if:
             * - confirmation is required, but someone didn't accept it yet.
             * - timer was paused by user, and it should be started again.
             */
            WAITING
        }
    }

    class Inactive(
        timerId: TimersRepository.TimerId,
        name: TimerName,
        ownerId: UsersRepository.UserId,
        settings: TimersRepository.Settings
    ) : DetailedTimer(timerId, name, ownerId, settings)
}

fun TimersRepository.Timer.toDetailed(sessionInfo: DetailedTimer.Active.SessionInfo?): DetailedTimer {
    return when(sessionInfo) {
        null -> DetailedTimer.Inactive(timerId, name, ownerId, settings)
        else -> DetailedTimer.Active(timerId, name, ownerId, settings, sessionInfo)
    }
}