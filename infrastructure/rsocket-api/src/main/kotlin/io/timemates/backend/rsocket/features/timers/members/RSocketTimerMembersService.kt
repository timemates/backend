package io.timemates.backend.rsocket.features.timers.members

import io.timemates.backend.pagination.PageToken
import io.timemates.backend.rsocket.features.authorization.providers.provideAuthorizationContext
import io.timemates.backend.rsocket.features.common.RSocketFailureCode
import io.timemates.backend.rsocket.internal.createOrFail
import io.timemates.backend.rsocket.internal.failRequest
import io.timemates.backend.rsocket.internal.markers.RSocketService
import io.timemates.backend.rsocket.features.timers.members.requests.GetMembersListRequest
import io.timemates.backend.rsocket.features.timers.members.requests.KickMemberRequest
import io.timemates.backend.serializable.types.users.serializable
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.timers.usecases.members.GetMembersUseCase
import io.timemates.backend.timers.usecases.members.KickTimerUserUseCase
import io.timemates.backend.users.types.User
import io.timemates.backend.users.types.value.UserId

class RSocketTimerMembersService(
    private val getMembersUseCase: GetMembersUseCase,
    private val kickTimerUserUseCase: KickTimerUserUseCase,
) : RSocketService {
    suspend fun getMembers(
        request: GetMembersListRequest
    ): GetMembersListRequest.Result = provideAuthorizationContext {
        val result = getMembersUseCase.execute(
            timerId = TimerId.createOrFail(request.timerId),
            pageToken = request.pageToken?.let { PageToken.accept(it) },
        )

        when (result) {
            is GetMembersUseCase.Result.Success -> GetMembersListRequest.Result(
                list = result.list.map(User::serializable),
                nextPageToken = result.nextPageToken?.forPublic()
            )
            is GetMembersUseCase.Result.NoAccess ->
                failRequest(RSocketFailureCode.NOT_FOUND, "Timer not found.")
        }
    }

    suspend fun kickMember(request: KickMemberRequest): Unit = provideAuthorizationContext {
        val result = kickTimerUserUseCase.execute(
            timerId = TimerId.createOrFail(request.timerId),
            userToKick = UserId.createOrFail(request.userId),
        )

        when (result) {
            is KickTimerUserUseCase.Result.Success -> {}
            is KickTimerUserUseCase.Result.NoAccess ->
                failRequest(RSocketFailureCode.FORBIDDEN, "You have no right to kick users.")
        }
    }
}