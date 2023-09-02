package io.timemates.backend.rsocket

import io.rsocket.kotlin.ConnectionAcceptor
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.RSocketError
import io.rsocket.kotlin.RSocketRequestHandler
import io.rsocket.kotlin.payload.Payload
import io.timemates.api.users.requests.EditUserRequestOuterClass.EditUserRequest
import io.timemates.backend.rsocket.authorization.RSocketAuthorizationsService
import io.timemates.backend.rsocket.authorization.types.requests.ConfigureAccountRequest
import io.timemates.backend.rsocket.authorization.types.requests.ConfirmAuthorizationRequest
import io.timemates.backend.rsocket.authorization.types.requests.GetAuthorizationsRequest
import io.timemates.backend.rsocket.authorization.types.requests.StartAuthorizationRequest
import io.timemates.backend.rsocket.common.RSocketFailureCode
import io.timemates.backend.rsocket.internal.asPayload
import io.timemates.backend.rsocket.internal.decoding
import io.timemates.backend.rsocket.internal.route
import io.timemates.backend.rsocket.timers.members.invites.requests.CreateInviteRequest
import io.timemates.backend.rsocket.timers.members.invites.requests.GetInvitesListRequest
import io.timemates.backend.rsocket.timers.members.invites.requests.JoinTimerByCodeRequest
import io.timemates.backend.rsocket.timers.members.invites.requests.RemoveInviteRequest
import io.timemates.backend.rsocket.timers.members.requests.GetMembersListRequest
import io.timemates.backend.rsocket.timers.members.requests.KickMemberRequest
import io.timemates.backend.rsocket.timers.sessions.requests.ConfirmSessionRequest
import io.timemates.backend.rsocket.timers.sessions.requests.JoinSessionRequest
import io.timemates.backend.rsocket.timers.sessions.requests.StartSessionRequest
import io.timemates.backend.rsocket.timers.sessions.requests.StopSessionRequest
import io.timemates.backend.rsocket.timers.types.requests.*
import io.timemates.backend.rsocket.users.requests.EditEmailRequest
import io.timemates.backend.rsocket.users.requests.GetUsersRequest
import kotlinx.coroutines.flow.flowOf

/**
 * Creates a ConnectionAcceptor for RSocket connections.
 *
 * @param auth The AuthorizationsService for handling authorization-related requests.
 * @return A ConnectionAcceptor instance.
 */
@Suppress("FunctionName")
internal fun RSocketConnectionAcceptor(
    auth: RSocketAuthorizationsService,
): ConnectionAcceptor {
    return ConnectionAcceptor {
        RSocketRequestHandler {
            requestResponse { payload ->
                when (val route = payload.route()) {
                    "auth.email.start" ->
                        payload.decoding<StartAuthorizationRequest> { auth.startAuthorizationViaEmail(it).asPayload() }

                    "auth.email.confirm" ->
                        payload.decoding<ConfirmAuthorizationRequest> { auth.confirmAuthorization(it).asPayload() }

                    "auth.account.configure" ->
                        payload.decoding<ConfigureAccountRequest> { auth.configureNewAccount(it).asPayload() }

                    "auth.get.list" ->
                        payload.decoding<GetAuthorizationsRequest> { auth.getAuthorizations(it).asPayload() }

                    "users.email.edit" -> payload.decoding<EditEmailRequest> { TODO() }
                    "users.profile.edit" -> payload.decoding<EditUserRequest> { TODO() }
                    "users.get.list" -> payload.decoding<GetUsersRequest> { TODO() }

                    "timers.create" -> payload.decoding<CreateTimerRequest> { TODO() }
                    "timers.get" -> payload.decoding<GetTimerRequest> { TODO() }
                    "timers.edit" -> payload.decoding<EditTimerRequest> { TODO() }
                    "timers.delete" -> payload.decoding<DeleteTimerRequest> { TODO() }
                    "timers.me.list" -> payload.decoding<GetUserTimersRequest> { TODO() }

                    "timers.members.list" -> payload.decoding<GetMembersListRequest> { TODO() }
                    "timers.members.kick" -> payload.decoding<KickMemberRequest> { TODO() }

                    "timers.members.invites.create" -> payload.decoding<CreateInviteRequest> { TODO() }
                    "timers.members.invites.join" -> payload.decoding<JoinTimerByCodeRequest> { TODO() }
                    "timers.members.invites.list" -> payload.decoding<GetInvitesListRequest> { TODO() }
                    "timers.members.invites.remove" -> payload.decoding<RemoveInviteRequest> { TODO() }

                    "timers.session.start" -> payload.decoding<StartSessionRequest> { TODO() }
                    "timers.session.stop" -> payload.decoding<StopSessionRequest> { TODO() }
                    "timers.session.join" -> payload.decoding<JoinSessionRequest> { TODO() }
                    "timers.session.leave" -> payload.decoding<JoinSessionRequest> { TODO() }
                    "timers.session.attendance.confirm" -> payload.decoding<ConfirmSessionRequest> { TODO() }

                    else -> failRoute(route)
                }
            }

            fireAndForget {
                when (val route = it.route()) {
                    "timers.session.ping" -> TODO()

                    else -> failRoute(route)
                }
            }

            requestStream { payload ->
                when (val route = payload.route()) {
                    "files.upload" -> flowOf(Payload.Empty) // todo

                    else -> failRoute(route)
                }
            }
        }
    }
}

context (RSocket)
private fun failRoute(route: String): Nothing {
    throw RSocketError.Custom(
        errorCode = RSocketFailureCode.NOT_FOUND.code,
        message = if (route.isBlank()) "Route cannot be empty or blank." else "Route '$route' is invalid."
    )
}