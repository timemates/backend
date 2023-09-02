package io.timemates.backend.timers.usecases.members.invites

import com.timemates.backend.time.TimeProvider
import com.timemates.backend.validation.createOrThrowInternally
import com.timemates.random.RandomProvider
import io.timemates.backend.common.markers.UseCase
import io.timemates.backend.common.types.value.Count
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.timers.repositories.TimerInvitesRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimersScope
import io.timemates.backend.timers.types.value.InviteCode
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.userId
import kotlin.time.Duration.Companion.minutes

class CreateInviteUseCase(
    private val invites: TimerInvitesRepository,
    private val timers: TimersRepository,
    private val randomProvider: RandomProvider,
    private val timeProvider: TimeProvider,
) : UseCase {
    context(AuthorizedContext<TimersScope.Write>)
    suspend fun execute(
        timerId: TimerId,
        limit: Count,
    ): Result {
        if (timers.getTimerInformation(timerId)?.ownerId != userId)
            return Result.NoAccess

        if (invites.getInvitesCount(timerId, timeProvider.provide() + 30.minutes).int > 10)
            return Result.TooManyCreation

        val code = InviteCode.createOrThrowInternally(randomProvider.randomHash(InviteCode.SIZE))
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