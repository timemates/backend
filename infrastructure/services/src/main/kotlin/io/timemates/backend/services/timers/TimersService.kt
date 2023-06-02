package io.timemates.backend.services.timers

import com.google.protobuf.Empty
import io.grpc.Status
import io.grpc.StatusException
import io.timemates.api.timers.TimersServiceGrpcKt
import io.timemates.api.timers.members.invites.requests.*
import io.timemates.api.timers.members.requests.GetMembersRequestKt
import io.timemates.api.timers.members.requests.GetMembersRequestOuterClass.GetMembersRequest
import io.timemates.api.timers.members.requests.KickMemberRequestOuterClass
import io.timemates.api.timers.requests.*
import io.timemates.api.timers.requests.CreateTimerRequestOuterClass.CreateTimerRequest
import io.timemates.api.timers.types.TimerOuterClass
import io.timemates.backend.common.types.value.Count
import io.timemates.backend.pagination.PageToken
import io.timemates.backend.pagination.map
import io.timemates.backend.services.authorization.context.provideAuthorizationContext
import io.timemates.backend.services.common.validation.createOrStatus
import io.timemates.backend.services.users.GrpcUsersMapper
import io.timemates.backend.timers.types.value.InviteCode
import io.timemates.backend.timers.types.value.TimerDescription
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.timers.types.value.TimerName
import io.timemates.backend.timers.usecases.*
import io.timemates.backend.timers.usecases.members.GetMembersUseCase
import io.timemates.backend.timers.usecases.members.KickTimerUserUseCase
import io.timemates.backend.timers.usecases.members.invites.CreateInviteUseCase
import io.timemates.backend.timers.usecases.members.invites.GetInvitesUseCase
import io.timemates.backend.timers.usecases.members.invites.RemoveInviteUseCase
import io.timemates.backend.users.types.value.UserId

class TimersService(
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
    private val mapper: GrpcTimersMapper,
    private val usersMapper: GrpcUsersMapper,
) : TimersServiceGrpcKt.TimersServiceCoroutineImplBase() {
    override suspend fun createInvite(
        request: CreateInviteRequest.InviteMemberRequest,
    ): CreateInviteRequest.InviteMemberRequest.Response = provideAuthorizationContext {
            val timerId = TimerId.createOrStatus(request.timerId)
            val count = Count.createOrStatus(request.maxJoiners)

            when (val result = createInviteUseCase.execute(timerId, count)) {
                CreateInviteUseCase.Result.NoAccess ->
                    throw StatusException(Status.NOT_FOUND)

                is CreateInviteUseCase.Result.Success -> InviteMemberRequestKt.response {
                    inviteCode = result.code.string
                }

                CreateInviteUseCase.Result.TooManyCreation ->
                    throw StatusException(Status.RESOURCE_EXHAUSTED)
            }
        }

    override suspend fun createTimer(
        request: CreateTimerRequest,
    ): CreateTimerRequest.Response = provideAuthorizationContext {
        val timerName = TimerName.createOrStatus(request.name)
        val timerDescription = TimerDescription.createOrStatus(request.description)
        val timerSettings = mapper.toDomainSettings(request.settings)

        when (val result = createTimerUseCase.execute(timerName, timerDescription, timerSettings)) {
            is CreateTimerUseCase.Result.Success -> CreateTimerRequestKt.response {
                timerId = result.timerId.long
            }
            CreateTimerUseCase.Result.TooManyCreations -> throw StatusException(Status.RESOURCE_EXHAUSTED)
        }
    }

    override suspend fun getInvites(
        request: GetInvitesRequestOuterClass.GetInvitesRequest,
    ): GetInvitesRequestOuterClass.GetInvitesRequest.Response = provideAuthorizationContext {
        val timerId = TimerId.createOrStatus(request.timerId)
        val pageToken = request.nextPageToken.takeIf { request.hasNextPageToken() }
            ?.let(PageToken::raw)

        when (val result = getInvitesUseCase.execute(timerId, pageToken)) {
            GetInvitesUseCase.Result.NoAccess -> throw StatusException(Status.PERMISSION_DENIED)
            is GetInvitesUseCase.Result.Success -> GetInvitesRequestKt.response {
                invites.addAll(result.list.map(mapper::toGrpcInvite).value)
                result.list.nextPageToken?.encoded()?.let { nextPageToken = it }
            }
        }
    }

    override suspend fun getMembers(
        request: GetMembersRequest
    ): GetMembersRequest.Response = provideAuthorizationContext {
        val timerId = TimerId.createOrStatus(request.timerId)
        val pageToken = request.nextPageToken
            .takeIf { request.hasNextPageToken() }
            ?.let { PageToken.raw(it) }

        when (val result = getMembersUseCase.execute(timerId, pageToken)) {
            GetMembersUseCase.Result.NoAccess -> throw StatusException(Status.PERMISSION_DENIED)
            is GetMembersUseCase.Result.Success -> GetMembersRequestKt.response {
                users.addAll(result.list.map(usersMapper::toGrpcUser))
                result.nextPageToken?.let { nextPageToken = it.encoded() }
            }
        }
    }

    override suspend fun getTimer(
        request: GetTimerRequestOuterClass.GetTimerRequest
    ): TimerOuterClass.Timer = provideAuthorizationContext {
        val timerId = TimerId.createOrStatus(request.timerId)

        when (val result = getTimerUseCase.execute(timerId)) {
            GetTimerUseCase.Result.NotFound -> throw StatusException(Status.NOT_FOUND)
            is GetTimerUseCase.Result.Success -> mapper.toGrpcTimer(result.timer)
        }
    }

    override suspend fun getTimers(
        request: GetTimersRequestOuterClass.GetTimersRequest
    ): GetTimersRequestOuterClass.GetTimersRequest.Response = provideAuthorizationContext {
        val pageToken = request.nextPageToken.takeIf { request.hasNextPageToken() }
            ?.let(PageToken::raw)

        when (val result = getTimersUseCase.execute(pageToken)) {
            is GetTimersUseCase.Result.Success -> GetTimersRequestKt.response {
                timers.addAll(result.list.map(mapper::toGrpcTimer).value)
                result.list.nextPageToken?.let { nextPageToken = it.encoded() }
            }
        }
    }

    override suspend fun kickMember(
        request: KickMemberRequestOuterClass.KickMemberRequest
    ): Empty = provideAuthorizationContext {
        val timerId = TimerId.createOrStatus(request.userId)
        val userId = UserId.createOrStatus(request.userId)

        when(kickTimerUserUseCase.execute(timerId, userId)) {
            KickTimerUserUseCase.Result.NoAccess -> throw StatusException(Status.NOT_FOUND)
            KickTimerUserUseCase.Result.Success -> Empty.getDefaultInstance()
        }
    }

    override suspend fun removeInvite(
        request: RemoveInviteRequestOuterClass.RemoveInviteRequest
    ): Empty = provideAuthorizationContext {
        val timerId = TimerId.createOrStatus(request.timerId)
        val code = InviteCode.createOrStatus(request.inviteCode)

        when (removeInviteUseCase.execute(timerId, code)) {
            RemoveInviteUseCase.Result.NoAccess -> throw StatusException(Status.PERMISSION_DENIED)
            RemoveInviteUseCase.Result.NotFound -> throw StatusException(Status.NOT_FOUND)
            RemoveInviteUseCase.Result.Success -> Empty.getDefaultInstance()
        }
    }

    override suspend fun removeTimer(
        request: RemoveTimerRequestOuterClass.RemoveTimerRequest
    ): Empty = provideAuthorizationContext {
        val timerId = TimerId.createOrStatus(request.timerId)

        when(removeTimerUseCase.execute(timerId)) {
            RemoveTimerUseCase.Result.NotFound -> throw StatusException(Status.NOT_FOUND)
            RemoveTimerUseCase.Result.Success -> Empty.getDefaultInstance()
        }
    }

    override suspend fun setTimerInfo(
        request: EditTimerInfoRequestOuterClass.EditTimerInfoRequest,
    ): Empty = provideAuthorizationContext {
        val timerId = TimerId.createOrStatus(request.timerId)
        val patch = mapper.toTimerInfoPatch(request)

        return when (setTimerInfoUseCase.execute(timerId, patch)) {
            SetTimerInfoUseCase.Result.Success -> Empty.getDefaultInstance()
            SetTimerInfoUseCase.Result.NoAccess -> throw StatusException(Status.PERMISSION_DENIED)
            SetTimerInfoUseCase.Result.NotFound -> throw StatusException(Status.NOT_FOUND)
        }
    }

    override suspend fun setTimerSettings(
        request: EditTimerSettingsRequestOuterClass.EditTimerSettingsRequest
    ): Empty = provideAuthorizationContext {
        val timerId = TimerId.createOrStatus(request.timerId)
        val patch = mapper.toTimerSettingsPatch(request)

        when (setTimerSettingsUseCase.execute(timerId, patch)) {
            SetTimerSettingsUseCase.Result.NoAccess -> throw StatusException(Status.PERMISSION_DENIED)
            SetTimerSettingsUseCase.Result.Success -> Empty.getDefaultInstance()
        }
    }
}