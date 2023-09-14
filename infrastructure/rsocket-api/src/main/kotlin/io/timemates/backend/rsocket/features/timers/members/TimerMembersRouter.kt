package io.timemates.backend.rsocket.features.timers.members

import io.timemates.backend.rsocket.internal.asPayload
import io.timemates.backend.rsocket.internal.decoding
import io.timemates.backend.rsocket.router.builders.DeclarableRoutingBuilder
import io.timemates.backend.rsocket.router.builders.requestResponse
import io.timemates.backend.rsocket.features.timers.members.invites.RSocketTimerInvitesService
import io.timemates.backend.rsocket.features.timers.members.invites.timerInvites
import io.timemates.backend.rsocket.features.timers.members.requests.GetMembersListRequest
import io.timemates.backend.rsocket.features.timers.members.requests.KickMemberRequest

fun DeclarableRoutingBuilder.timerMembers(
    members: RSocketTimerMembersService,
    invites: RSocketTimerInvitesService,
): Unit = route("members") {
    timerInvites(invites)

    requestResponse("list") { payload ->
        payload.decoding<GetMembersListRequest> { members.getMembers(it).asPayload() }
    }

    requestResponse("kick") { payload ->
        payload.decoding<KickMemberRequest> { members.kickMember(it).asPayload() }
    }
}