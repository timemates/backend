package io.timemates.api.rsocket.serializable.requests.authorizations

import io.timemates.api.rsocket.serializable.requests.common.RSocketRequest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class TerminateAuthorizationRequest<R : Any> : RSocketRequest<R> {
    /**
     * This type of termination request terminates authorization with which
     * user has sent termination request.
     */
    @SerialName("current")
    data object Current : TerminateAuthorizationRequest<Current.Result>(), RSocketRequest.Key<Current> {

        override val key: RSocketRequest.Key<*>
            get() = this

        data object Result
    }
}