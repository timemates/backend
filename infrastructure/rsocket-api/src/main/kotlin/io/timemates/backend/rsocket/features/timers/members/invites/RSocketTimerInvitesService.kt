package io.timemates.backend.rsocket.features.timers.members.invites

import io.timemates.api.rsocket.serializable.requests.timers.members.invites.CreateInviteRequest
import io.timemates.api.rsocket.serializable.requests.timers.members.invites.GetInvitesListRequest
import io.timemates.api.rsocket.serializable.requests.timers.members.invites.JoinTimerByCodeRequest
import io.timemates.api.rsocket.serializable.requests.timers.members.invites.RemoveInviteRequest
import io.timemates.backend.common.types.value.Count
import io.timemates.backend.pagination.PageToken
import io.timemates.backend.rsocket.features.common.RSocketFailureCode
import io.timemates.backend.rsocket.features.common.providers.provideAuthorizationContext
import io.timemates.backend.rsocket.internal.createOrFail
import io.timemates.backend.rsocket.internal.failRequest
import io.timemates.backend.rsocket.internal.markers.RSocketService
import io.timemates.backend.serializable.types.timers.members.invites.serializable
import io.timemates.backend.timers.types.value.InviteCode
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.timers.usecases.members.invites.CreateInviteUseCase
import io.timemates.backend.timers.usecases.members.invites.GetInvitesUseCase
import io.timemates.backend.timers.usecases.members.invites.JoinByInviteUseCase
import io.timemates.backend.timers.usecases.members.invites.RemoveInviteUseCase

class RSocketTimerInvitesService(
    private val createInviteUseCase: CreateInviteUseCase,
    private val removeInviteUseCase: RemoveInviteUseCase,
    private val getInvitesUseCase: GetInvitesUseCase,
    private val joinByInviteUseCase: JoinByInviteUseCase,
) : RSocketService {
    suspend fun createInvite(
        request: CreateInviteRequest,
    ): CreateInviteRequest.Result = provideAuthorizationContext {
        val result = createInviteUseCase.execute(
            timerId = TimerId.createOrFail(request.timerId),
            limit = Count.createOrFail(request.maxJoiners)
        )

        when (result) {
            is CreateInviteUseCase.Result.Success -> CreateInviteRequest.Result(result.code.string)
            is CreateInviteUseCase.Result.NoAccess -> failRequest(
                failureCode = RSocketFailureCode.FORBIDDEN,
                message = "No permission to issue an invite",
            )

            is CreateInviteUseCase.Result.TooManyCreation -> failRequest(
                failureCode = RSocketFailureCode.TOO_MANY_REQUESTS,
                message = "Too many creations of invite"
            )
        }
    }

    suspend fun getInvites(
        request: GetInvitesListRequest,
    ): GetInvitesListRequest.Result = provideAuthorizationContext {
        val result = getInvitesUseCase.execute(
            timerId = TimerId.createOrFail(request.timerId),
            pageToken = request.pageToken?.let { PageToken.accept(it) },
        )

        when (result) {
            is GetInvitesUseCase.Result.Success -> GetInvitesListRequest.Result(
                invites = result.page.value.map { it.serializable() },
                nextPageToken = result.page.nextPageToken?.forPublic(),
            )

            is GetInvitesUseCase.Result.NoAccess -> failRequest(
                failureCode = RSocketFailureCode.FORBIDDEN,
                message = "You have no access to invites.",
            )
        }
    }

    suspend fun joinTimerByCode(
        request: JoinTimerByCodeRequest,
    ): JoinTimerByCodeRequest.Result = provideAuthorizationContext {
        val result = joinByInviteUseCase.execute(
            code = InviteCode.createOrFail(request.code)
        )

        when (result) {
            is JoinByInviteUseCase.Result.Success -> JoinTimerByCodeRequest.Result(
                timerId = result.timerId.long,
            )

            is JoinByInviteUseCase.Result.NotFound -> failRequest(
                failureCode = RSocketFailureCode.NOT_FOUND,
                message = "Invite code is invalid.",
            )
        }
    }

    suspend fun removeInvite(
        request: RemoveInviteRequest,
    ): Unit = provideAuthorizationContext {
        val result = removeInviteUseCase.execute(
            timerId = TimerId.createOrFail(request.timerId),
            code = InviteCode.createOrFail(request.code),
        )

        when (result) {
            is RemoveInviteUseCase.Result.Success -> {}
            is RemoveInviteUseCase.Result.NoAccess -> failRequest(
                failureCode = RSocketFailureCode.FORBIDDEN,
                message = "No permission to remove invites",
            )

            is RemoveInviteUseCase.Result.NotFound -> failRequest(
                failureCode = RSocketFailureCode.NOT_FOUND,
                message = "Invite not found",
            )
        }
    }
}