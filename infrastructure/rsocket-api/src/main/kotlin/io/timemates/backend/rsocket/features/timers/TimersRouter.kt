package io.timemates.backend.rsocket.features.timers

import com.y9vad9.rsocket.router.annotations.ExperimentalRouterApi
import com.y9vad9.rsocket.router.builders.RoutingBuilder
import com.y9vad9.rsocket.router.serialization.requestResponse
import io.timemates.api.rsocket.serializable.requests.timers.*
import io.timemates.backend.rsocket.features.timers.members.RSocketTimerMembersService
import io.timemates.backend.rsocket.features.timers.members.invites.RSocketTimerInvitesService
import io.timemates.backend.rsocket.features.timers.members.timerMembers
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

    requestResponse("create") { data: CreateTimerRequest ->
        timers.createTimer(data)
    }

    requestResponse("get") { data: GetTimerRequest ->
        timers.getTimer(data)
    }

    requestResponse("edit") { data: EditTimerRequest ->
        timers.editTimer(data)
    }

    requestResponse("delete") { data: DeleteTimerRequest ->
        timers.deleteTimer(data)
    }

    requestResponse("user.list") { data: GetUserTimersRequest ->
        timers.getUserTimers(data)
    }
}