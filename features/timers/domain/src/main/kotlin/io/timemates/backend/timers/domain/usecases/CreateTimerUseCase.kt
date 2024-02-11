package io.timemates.backend.timers.domain.usecases

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.core.types.integration.auth.userId
import io.timemates.backend.foundation.authorization.Authorized
import io.timemates.backend.timers.domain.repositories.TimersRepository
import io.timemates.backend.types.timers.TimerSettings
import io.timemates.backend.types.timers.TimersScope
import io.timemates.backend.types.timers.value.TimerDescription
import io.timemates.backend.types.timers.value.TimerId
import io.timemates.backend.types.timers.value.TimerName
import kotlin.time.Duration.Companion.minutes

class CreateTimerUseCase(
    private val timers: TimersRepository,
    private val time: TimeProvider,
) {
    suspend fun execute(
        auth: Authorized<TimersScope.Write>,
        name: TimerName,
        description: TimerDescription,
        settings: TimerSettings,
    ): Result {
        val userId = auth.userId
        val currentTime = time.provide()
        return if (timers.getOwnedTimersCount(userId, after = currentTime - 30.minutes) > 20) {
            Result.TooManyCreations
        } else {
            val timerId = timers.createTimer(name, description, settings, userId, time.provide())
            timers.addMember(userId, timerId, currentTime, null)
            Result.Success(timerId)
        }
    }

    sealed interface Result {
        @JvmInline
        value class Success(val timerId: TimerId) : Result

        data object TooManyCreations : Result
    }
}