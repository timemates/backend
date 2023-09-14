package io.timemates.backend.rsocket.features.timers.members.invites

import io.timemates.backend.rsocket.internal.asPayload
import io.timemates.backend.rsocket.internal.decoding
import io.timemates.backend.rsocket.router.builders.DeclarableRoutingBuilder
import io.timemates.backend.rsocket.router.builders.requestResponse
import io.timemates.backend.rsocket.features.timers.members.invites.requests.CreateInviteRequest
import io.timemates.backend.rsocket.features.timers.members.invites.requests.GetInvitesListRequest
import io.timemates.backend.rsocket.features.timers.members.invites.requests.JoinTimerByCodeRequest
import io.timemates.backend.rsocket.features.timers.members.invites.requests.RemoveInviteRequest

fun DeclarableRoutingBuilder.timerInvites(
    invites: RSocketTimerInvitesService,
): Unit = route("invites") {
    requestResponse("create") { payload ->
        payload.decoding<CreateInviteRequest> { invites.createInvite(it).asPayload() }
    }

    requestResponse("join") { payload ->
        payload.decoding<JoinTimerByCodeRequest> { invites.joinTimerByCode(it).asPayload() }
    }

    requestResponse("list") { payload ->
        payload.decoding<GetInvitesListRequest> { invites.getInvites(it).asPayload() }
    }

    requestResponse("remove") { payload ->
        payload.decoding<RemoveInviteRequest> { invites.removeInvite(it).asPayload() }
    }
}