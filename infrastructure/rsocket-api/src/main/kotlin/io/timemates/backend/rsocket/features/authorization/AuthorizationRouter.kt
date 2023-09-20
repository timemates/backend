package io.timemates.backend.rsocket.features.authorization

import com.y9vad9.rsocket.router.annotations.ExperimentalRouterApi
import com.y9vad9.rsocket.router.builders.RoutingBuilder
import com.y9vad9.rsocket.router.builders.requestResponse
import io.timemates.backend.rsocket.features.authorization.requests.*
import io.timemates.backend.rsocket.internal.asPayload
import io.timemates.backend.rsocket.internal.decoding

/**
 * Sets up the route handlers for authorizations.
 *
 * @param auth The RSocketAuthorizationsService instance used to handle authorization requests.
 */
@OptIn(ExperimentalRouterApi::class)
fun RoutingBuilder.authorizations(
    auth: RSocketAuthorizationsService,
): Unit = route("authorizations") {
    route("email") {
        requestResponse("start") { payload ->
            payload.decoding<StartAuthorizationRequest> { auth.startAuthorizationViaEmail(it).asPayload() }
        }

        requestResponse("confirm") { payload ->
            payload.decoding<ConfirmAuthorizationRequest> { auth.confirmAuthorization(it).asPayload() }
        }
    }

    route("account") {
        requestResponse("configure") { payload ->
            payload.decoding<ConfigureAccountRequest> { auth.configureNewAccount(it).asPayload() }
        }
    }

    requestResponse("list") { payload ->
        payload.decoding<GetAuthorizationsRequest> { auth.getAuthorizations(it).asPayload() }
    }

    requestResponse("renew") { payload ->
        payload.decoding<RenewAuthorizationRequest> { auth.renewAuthorization(it).asPayload() }
    }
}