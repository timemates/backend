package io.timemates.backend.rsocket.features.authorization

import com.y9vad9.rsocket.router.annotations.ExperimentalRouterApi
import com.y9vad9.rsocket.router.builders.RoutingBuilder
import com.y9vad9.rsocket.router.serialization.requestResponse
import io.timemates.api.rsocket.serializable.requests.authorizations.*

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
        requestResponse("start") { data: StartAuthorizationRequest ->
            auth.startAuthorizationViaEmail(data)
        }

        requestResponse("confirm") { data: ConfirmAuthorizationRequest ->
            auth.confirmAuthorization(data)
        }
    }

    route("account") {
        requestResponse("configure") { data: ConfigureAccountRequest ->
            auth.configureNewAccount(data)
        }
    }

    requestResponse("list") { data: GetAuthorizationsRequest ->
        auth.getAuthorizations(data)
    }

    requestResponse("terminate") { data: TerminateAuthorizationRequest ->
        auth.terminateAuthorization(data)
    }

    requestResponse("renew") { data: RenewAuthorizationRequest ->
        auth.renewAuthorization(data)
    }
}