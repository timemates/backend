package io.timemates.backend.rsocket.features.timers

import io.timemates.backend.pagination.PageToken
import io.timemates.backend.rsocket.features.common.RSocketFailureCode
import io.timemates.backend.rsocket.features.common.providers.provideAuthorizationContext
import io.timemates.backend.rsocket.features.timers.requests.*
import io.timemates.backend.rsocket.internal.createOrFail
import io.timemates.backend.rsocket.internal.failRequest
import io.timemates.backend.rsocket.internal.markers.RSocketService
import io.timemates.backend.serializable.types.timers.serializable
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.Timer
import io.timemates.backend.timers.types.TimerSettings
import io.timemates.backend.timers.types.value.TimerDescription
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.timers.types.value.TimerName
import io.timemates.backend.timers.usecases.*
import io.timemates.backend.timers.usecases.members.GetMembersUseCase
import io.timemates.backend.timers.usecases.members.KickTimerUserUseCase
import io.timemates.backend.timers.usecases.members.invites.CreateInviteUseCase
import io.timemates.backend.timers.usecases.members.invites.GetInvitesUseCase
import io.timemates.backend.timers.usecases.members.invites.RemoveInviteUseCase

class RSocketTimersService(
    private val createInviteUseCase: CreateInviteUseCase,
    private val createTimerUseCase: CreateTimerUseCase,
    private val removeTimerUseCase: RemoveTimerUseCase,
    private val setTimerInfoUseCase: SetTimerInfoUseCase,
    private val getInvitesUseCase: GetInvitesUseCase,
    private val getMembersUseCase: GetMembersUseCase,
    private val getTimersUseCase: GetTimersUseCase,
    private val setTimerSettingsUseCase: SetTimerSettingsUseCase,
    private val kickTimerUserUseCase: KickTimerUserUseCase,
    private val removeInviteUseCase: RemoveInviteUseCase,
    private val getTimerUseCase: GetTimerUseCase,
    private val mapper: RSocketTimersMapper,
) : RSocketService {

    suspend fun createTimer(request: CreateTimerRequest): CreateTimerRequest.Result = provideAuthorizationContext {
        val result = createTimerUseCase.execute(
            name = TimerName.createOrFail(request.name),
            description = TimerDescription.createOrFail(request.description),
            settings = request.settings?.let { mapper.toCoreSettings(it) } ?: TimerSettings.Default,
        )

        when (result) {
            is CreateTimerUseCase.Result.Success -> CreateTimerRequest.Result(result.timerId.long)
            is CreateTimerUseCase.Result.TooManyCreations ->
                failRequest(RSocketFailureCode.TOO_MANY_REQUESTS, "You can't create that much timers.")
        }
    }

    suspend fun getTimer(request: GetTimerRequest): GetTimerRequest.Result = provideAuthorizationContext {
        val result = getTimerUseCase.execute(TimerId.createOrFail(request.timerId))

        when (result) {
            is GetTimerUseCase.Result.Success -> GetTimerRequest.Result(result.timer.serializable())
            is GetTimerUseCase.Result.NotFound ->
                failRequest(RSocketFailureCode.NOT_FOUND, TIMER_NOT_FOUND)
        }
    }

    suspend fun deleteTimer(request: DeleteTimerRequest): Unit = provideAuthorizationContext {
        val result = removeTimerUseCase.execute(TimerId.createOrFail(request.timerId))

        when (result) {
            is RemoveTimerUseCase.Result.Success -> {}
            is RemoveTimerUseCase.Result.NotFound -> failRequest(RSocketFailureCode.NOT_FOUND, TIMER_NOT_FOUND)
        }
    }

    suspend fun editTimer(request: EditTimerRequest): Unit = provideAuthorizationContext {
        val result = setTimerInfoUseCase.execute(
            timerId = TimerId.createOrFail(request.timerId),
            patch = TimersRepository.TimerInformation.Patch(
                name = request.name?.let { TimerName.createOrFail(it) },
                description = request.description?.let { TimerDescription.createOrFail(it) }
            ),
            newSettings = request.settings?.let { mapper.toCoreSettingsPatch(it) },
        )

        when (result) {
            is SetTimerInfoUseCase.Result.Success -> {}
            is SetTimerInfoUseCase.Result.NotFound -> failRequest(RSocketFailureCode.NOT_FOUND, TIMER_NOT_FOUND)
            is SetTimerInfoUseCase.Result.NoAccess ->
                failRequest(
                    failureCode = RSocketFailureCode.FORBIDDEN,
                    message = "You can't change timer details; no such access"
                )
        }
    }

    suspend fun getUserTimers(request: GetUserTimersRequest): GetUserTimersRequest.Result = provideAuthorizationContext {
        val pageToken = request.pageToken?.let { PageToken.accept(it) }
        val result = getTimersUseCase.execute(pageToken)

        when (result) {
            is GetTimersUseCase.Result.Success -> GetUserTimersRequest.Result(
                result.page.nextPageToken?.forPublic(), result.page.value.map(Timer::serializable),
            )
        }
    }
}

private const val TIMER_NOT_FOUND = "Timer not found."