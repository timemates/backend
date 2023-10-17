package io.timemates.backend.rsocket.features.timers.members.invites

import com.y9vad9.rsocket.router.builders.DeclarableRoutingBuilder
import com.y9vad9.rsocket.router.serialization.requestResponse
import io.timemates.api.rsocket.serializable.requests.timers.members.invites.CreateInviteRequest
import io.timemates.api.rsocket.serializable.requests.timers.members.invites.GetInvitesListRequest
import io.timemates.api.rsocket.serializable.requests.timers.members.invites.JoinTimerByCodeRequest
import io.timemates.api.rsocket.serializable.requests.timers.members.invites.RemoveInviteRequest

fun DeclarableRoutingBuilder.timerInvites(
    invites: RSocketTimerInvitesService,
): Unit = route("invites") {
    requestResponse("create") { data: CreateInviteRequest ->
        invites.createInvite(data)
    }

    requestResponse("join") { data: JoinTimerByCodeRequest ->
        invites.joinTimerByCode(data)
    }

    requestResponse("list") { data: GetInvitesListRequest ->
        invites.getInvites(data)
    }

    requestResponse("remove") { data: RemoveInviteRequest ->
        invites.removeInvite(data)
    }
}