package io.timemates.backend.services.timers

import io.timemates.api.common.types.StatusOuterClass
import io.timemates.api.timers.TimersServiceGrpcKt
import io.timemates.api.timers.requests.*
import io.timemates.api.timers.requests.members.GetMembersRequestOuterClass
import io.timemates.api.timers.requests.members.KickMemberRequestOuterClass
import io.timemates.api.timers.requests.members.invites.CreateInviteRequest
import io.timemates.api.timers.requests.members.invites.GetInvitesRequestOuterClass
import io.timemates.api.timers.requests.members.invites.RemoveInviteRequestOuterClass
import io.timemates.api.timers.types.TimerOuterClass

class TimersService : TimersServiceGrpcKt.TimersServiceCoroutineImplBase() {
    override suspend fun createInvite(request: CreateInviteRequest.InviteMemberRequest): CreateInviteRequest.InviteMemberRequest.Response {
        TODO()
    }

    override suspend fun createTimer(request: CreateTimerRequestOuterClass.CreateTimerRequest): StatusOuterClass.Status {
        TODO()
    }

    override suspend fun getInvites(request: GetInvitesRequestOuterClass.GetInvitesRequest): GetInvitesRequestOuterClass.GetInvitesRequest.Response {
        TODO()
    }

    override suspend fun getMembers(request: GetMembersRequestOuterClass.GetMembersRequest): GetMembersRequestOuterClass.GetMembersRequest.Response {
        TODO()
    }

    override suspend fun getTimer(request: GetTimerRequestOuterClass.GetTimerRequest): TimerOuterClass.Timer {
        TODO()
    }

    override suspend fun getTimers(request: GetTimersRequestOuterClass.GetTimersRequest): GetTimersRequestOuterClass.GetTimersRequest {
        TODO()
    }

    override suspend fun kickMember(request: KickMemberRequestOuterClass.KickMemberRequest): StatusOuterClass.Status {
        TODO()
    }

    override suspend fun removeInvite(request: RemoveInviteRequestOuterClass.RemoveInviteRequest): StatusOuterClass.Status {
        TODO()
    }

    override suspend fun removeTimer(request: RemoveTimerRequestOuterClass.RemoveTimerRequest): StatusOuterClass.Status {
        TODO()
    }

    override suspend fun setTimerInfo(request: EditTimerInfoRequestOuterClass.EditTimerInfoRequest): StatusOuterClass.Status {
        TODO()
    }

    override suspend fun setTimerSettings(request: EditTimerSettingsRequestOuterClass.EditTimerSettingsRequest): StatusOuterClass.Status {
        TODO()
    }
}