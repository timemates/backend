package io.timemates.backend.rsocket.features.timers

import io.timemates.backend.rsocket.internal.asPayload
import io.timemates.backend.rsocket.internal.decoding
import io.timemates.backend.rsocket.router.annotations.ExperimentalRouterApi
import io.timemates.backend.rsocket.router.builders.RoutingBuilder
import io.timemates.backend.rsocket.router.builders.requestResponse
import io.timemates.backend.rsocket.features.timers.members.RSocketTimerMembersService
import io.timemates.backend.rsocket.features.timers.members.invites.RSocketTimerInvitesService
import io.timemates.backend.rsocket.features.timers.members.timerMembers
import io.timemates.backend.rsocket.features.timers.requests.*
import io.timemates.backend.rsocket.features.timers.sessions.RSocketTimerSessionsService
import io.timemates.backend.rsocket.features.timers.sessions.timerSessions

@OptIn(ExperimentalRouterApi::class)
fun RoutingBuilder.timers(
    timers: RSocketTimersService,
    members: RSocketTimerMembersService,
    invites: RSocketTimerInvitesService,
    sessions: RSocketTimerSessionsService,
): Unit = route("timers") {
    timerSessions(sessions)
    timerMembers(members, invites)

    requestResponse("create") { payload ->
        payload.decoding<CreateTimerRequest> { timers.createTimer(it).asPayload() }
    }

    requestResponse("get") { payload ->
        payload.decoding<GetTimerRequest> { timers.getTimer(it).asPayload() }
    }

    requestResponse("timers.edit") { payload ->
        payload.decoding<EditTimerRequest> { timers.editTimer(it).asPayload() }
    }

    requestResponse("delete") { payload ->
        payload.decoding<DeleteTimerRequest> { timers.deleteTimer(it).asPayload() }
    }

    requestResponse("user.list") { payload ->
        payload.decoding<GetUserTimersRequest> { timers.getUserTimers(it).asPayload() }
    }
}