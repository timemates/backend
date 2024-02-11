package io.timemates.backend.timers.domain.usecases.members.invites

import com.timemates.backend.time.TimeProvider
import com.timemates.random.RandomProvider
import io.timemates.backend.core.types.integration.auth.userId
import io.timemates.backend.foundation.authorization.Authorized
import io.timemates.backend.timers.domain.repositories.TimerInvitesRepository
import io.timemates.backend.timers.domain.repositories.TimersRepository
import io.timemates.backend.types.common.value.Count
import io.timemates.backend.types.timers.TimersScope
import io.timemates.backend.types.timers.value.InviteCode
import io.timemates.backend.types.timers.value.TimerId
import io.timemates.backend.validation.annotations.ValidationDelicateApi
import io.timemates.backend.validation.createUnsafe
import kotlin.time.Duration.Companion.minutes

class CreateInviteUseCase(
    private val invites: TimerInvitesRepository,
    private val timers: TimersRepository,
    private val randomProvider: RandomProvider,
    private val timeProvider: TimeProvider,
) {

    suspend fun execute(
        auth: Authorized<TimersScope.Write>,
        timerId: TimerId,
        limit: Count,
    ): Result {
        val userId = auth.userId
        if (timers.getTimerInformation(timerId)?.ownerId != userId)
            return Result.NoAccess

        if (invites.getInvitesCount(timerId, timeProvider.provide() + 30.minutes).int > 10)
            return Result.TooManyCreation

        @OptIn(ValidationDelicateApi::class)
        val code = InviteCode.createUnsafe(randomProvider.randomHash(InviteCode.SIZE))

        invites.createInvite(timerId, userId, code, timeProvider.provide(), limit)
        return Result.Success(code)
    }

    sealed interface Result {
        @JvmInline
        value class Success(val code: InviteCode) : Result
        data object NoAccess : Result
        data object TooManyCreation : Result
    }
}