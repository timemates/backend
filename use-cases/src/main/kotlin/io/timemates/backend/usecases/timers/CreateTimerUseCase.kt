package io.timemates.backend.usecases.timers

import io.timemates.backend.providers.CurrentTimeProvider
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.types.value.TimerName
import kotlin.time.Duration.Companion.minutes

class CreateTimerUseCase(
    private val timers: TimersRepository,
    private val time: CurrentTimeProvider
) {
    suspend operator fun invoke(
        userId: UsersRepository.UserId,
        settings: TimersRepository.Settings,
        name: TimerName
    ): Result {
        return if(timers.getOwnedTimersCount(userId, time.provide() + 30.minutes) > 20) {
            Result.TooManyCreations
        } else Result.Success(timers.createTimer(name, settings, userId, time.provide()))
    }

    sealed interface Result {
        @JvmInline
        value class Success(val timerId: TimersRepository.TimerId) : Result

        object TooManyCreations : Result
    }
}