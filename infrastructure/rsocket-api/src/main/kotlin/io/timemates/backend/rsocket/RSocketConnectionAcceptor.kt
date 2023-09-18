package io.timemates.backend.rsocket

import com.y9vad9.rsocket.router.annotations.ExperimentalRouterApi
import com.y9vad9.rsocket.router.router
import io.rsocket.kotlin.ConnectionAcceptor
import io.rsocket.kotlin.RSocketRequestHandler
import io.timemates.backend.rsocket.features.authorization.RSocketAuthorizationsService
import io.timemates.backend.rsocket.features.authorization.authorizations
import io.timemates.backend.rsocket.features.files.RSocketFilesService
import io.timemates.backend.rsocket.features.files.files
import io.timemates.backend.rsocket.features.timers.RSocketTimersService
import io.timemates.backend.rsocket.features.timers.members.RSocketTimerMembersService
import io.timemates.backend.rsocket.features.timers.members.invites.RSocketTimerInvitesService
import io.timemates.backend.rsocket.features.timers.sessions.RSocketTimerSessionsService
import io.timemates.backend.rsocket.features.timers.timers
import io.timemates.backend.rsocket.features.users.RSocketUsersService
import io.timemates.backend.rsocket.features.users.users
import io.timemates.backend.rsocket.interceptors.AuthorizableRouteContext
import io.timemates.backend.rsocket.interceptors.AuthorizableRoutedRequesterInterceptor
import kotlin.coroutines.coroutineContext

/**
 * Creates a ConnectionAcceptor for RSocket connections.
 *
 * @param auth The AuthorizationsService for handling authorization-related requests.
 * @return A ConnectionAcceptor instance.
 */
@OptIn(ExperimentalRouterApi::class)
@Suppress("FunctionName")
internal fun RSocketConnectionAcceptor(
    auth: RSocketAuthorizationsService,
    users: RSocketUsersService,
    timers: RSocketTimersService,
    timerMembers: RSocketTimerMembersService,
    timerInvites: RSocketTimerInvitesService,
    timerSessions: RSocketTimerSessionsService,
    files: RSocketFilesService,
    requestInterceptor: AuthorizableRoutedRequesterInterceptor,
): ConnectionAcceptor {
    return ConnectionAcceptor {
        RSocketRequestHandler {
            router {
                preprocessors {
                    forCoroutineContext(requestInterceptor)
                }

                routeProvider { _ ->
                    coroutineContext[AuthorizableRouteContext]?.route
                        ?: error("Interceptor did not properly work.")
                }

                routing {
                    authorizations(auth)
                    users(users)
                    files(files)
                    timers(
                        timers = timers,
                        members = timerMembers,
                        invites = timerInvites,
                        sessions = timerSessions,
                    )
                }
            }
        }
    }
}