package org.timemates.backend.timers.domain.usecases.members.invites

import com.timemates.backend.time.TimeProvider
import com.timemates.random.RandomProvider
import org.timemates.backend.core.types.integration.auth.userId
import org.timemates.backend.foundation.authorization.Authorized
import org.timemates.backend.timers.domain.repositories.TimerInvitesRepository
import org.timemates.backend.timers.domain.repositories.TimersRepository
import org.timemates.backend.types.common.value.Count
import org.timemates.backend.types.timers.TimersScope
import org.timemates.backend.types.timers.value.InviteCode
import org.timemates.backend.types.timers.value.TimerId
import org.timemates.backend.validation.annotations.ValidationDelicateApi
import org.timemates.backend.validation.createUnsafe
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