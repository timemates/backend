package io.timemates.backend.timers.usecases

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.common.markers.UseCase
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimerSettings
import io.timemates.backend.timers.types.TimersScope
import io.timemates.backend.timers.types.value.TimerDescription
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.timers.types.value.TimerName
import io.timemates.backend.users.types.value.userId
import kotlin.time.Duration.Companion.minutes

class CreateTimerUseCase(
    private val timers: TimersRepository,
    private val time: TimeProvider,
) : UseCase {
    context(AuthorizedContext<TimersScope.Write>)
    suspend fun execute(
        name: TimerName,
        description: TimerDescription,
        settings: TimerSettings,
    ): Result {
        val currentTime = time.provide()
        return if (
            timers.getOwnedTimersCount(
                userId, after = currentTime - 30.minutes
            ) > 20
        ) {
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