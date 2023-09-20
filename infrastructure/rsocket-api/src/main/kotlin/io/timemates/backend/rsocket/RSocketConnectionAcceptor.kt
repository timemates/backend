package io.timemates.backend.rsocket

import com.y9vad9.rsocket.router.annotations.ExperimentalInterceptorsApi
import com.y9vad9.rsocket.router.annotations.ExperimentalRouterApi
import com.y9vad9.rsocket.router.installOn
import io.rsocket.kotlin.ConnectionAcceptor
import io.rsocket.kotlin.RSocketRequestHandler
import io.timemates.backend.rsocket.features.authorization.RSocketAuthorizationsService
import io.timemates.backend.rsocket.features.files.RSocketFilesService
import io.timemates.backend.rsocket.features.timers.RSocketTimersService
import io.timemates.backend.rsocket.features.timers.members.RSocketTimerMembersService
import io.timemates.backend.rsocket.features.timers.members.invites.RSocketTimerInvitesService
import io.timemates.backend.rsocket.features.timers.sessions.RSocketTimerSessionsService
import io.timemates.backend.rsocket.features.users.RSocketUsersService
import io.timemates.backend.rsocket.interceptors.AuthorizableRoutePreprocessor

/**
 * Creates a ConnectionAcceptor for RSocket connections.
 *
 * @param auth The AuthorizationsService for handling authorization-related requests.
 * @return A ConnectionAcceptor instance.
 */
@OptIn(ExperimentalRouterApi::class, ExperimentalInterceptorsApi::class)
@Suppress("FunctionName")
internal fun RSocketConnectionAcceptor(
    auth: RSocketAuthorizationsService,
    users: RSocketUsersService,
    timers: RSocketTimersService,
    timerMembers: RSocketTimerMembersService,
    timerInvites: RSocketTimerInvitesService,
    timerSessions: RSocketTimerSessionsService,
    files: RSocketFilesService,
    requestInterceptor: AuthorizableRoutePreprocessor,
): ConnectionAcceptor {
    return ConnectionAcceptor {
        RSocketRequestHandler {
            timeMatesRouter(
                auth = auth,
                users = users,
                timers = timers,
                timerMembers = timerMembers,
                timerInvites = timerInvites,
                timerSessions = timerSessions,
                files = files,
                requestInterceptor = requestInterceptor
            ).installOn(this)
        }
    }
}