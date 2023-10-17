package io.timemates.backend.rsocket.features.timers.members

import com.y9vad9.rsocket.router.builders.DeclarableRoutingBuilder
import com.y9vad9.rsocket.router.serialization.requestResponse
import io.timemates.api.rsocket.serializable.requests.timers.members.GetMembersListRequest
import io.timemates.api.rsocket.serializable.requests.timers.members.KickMemberRequest
import io.timemates.backend.rsocket.features.timers.members.invites.RSocketTimerInvitesService
import io.timemates.backend.rsocket.features.timers.members.invites.timerInvites

fun DeclarableRoutingBuilder.timerMembers(
    members: RSocketTimerMembersService,
    invites: RSocketTimerInvitesService,
): Unit = route("members") {
    timerInvites(invites)

    requestResponse("list") { data: GetMembersListRequest ->
        members.getMembers(data)
    }

    requestResponse("kick") { data: KickMemberRequest ->
        members.kickMember(data)
    }
}