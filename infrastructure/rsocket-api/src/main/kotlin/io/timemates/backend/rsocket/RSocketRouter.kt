package io.timemates.backend.rsocket

import com.y9vad9.rsocket.router.annotations.ExperimentalInterceptorsApi
import com.y9vad9.rsocket.router.router
import com.y9vad9.rsocket.router.serialization.json.JsonContentSerializer
import com.y9vad9.rsocket.router.serialization.preprocessor.serialization
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
import io.timemates.backend.rsocket.interceptors.AuthorizableRoutePreprocessor
import kotlin.coroutines.coroutineContext

@OptIn(ExperimentalInterceptorsApi::class)
fun timeMatesRouter(
    auth: RSocketAuthorizationsService,
    users: RSocketUsersService,
    timers: RSocketTimersService,
    timerMembers: RSocketTimerMembersService,
    timerInvites: RSocketTimerInvitesService,
    timerSessions: RSocketTimerSessionsService,
    files: RSocketFilesService,
    requestInterceptor: AuthorizableRoutePreprocessor,
) = router {
    preprocessors {
        forCoroutineContext(requestInterceptor)
    }

    routeProvider { _ ->
        coroutineContext[AuthorizableRouteContext]?.route
            ?: error("Interceptor did not properly work.")
    }

    serialization { _, _ ->
        JsonContentSerializer()
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