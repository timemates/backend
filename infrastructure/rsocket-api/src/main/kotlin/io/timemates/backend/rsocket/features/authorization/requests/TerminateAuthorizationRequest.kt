package io.timemates.backend.rsocket.features.authorization.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class TerminateAuthorizationRequest {
    /**
     * This type of termination request terminates authorization with which
     * user has sent termination request.
     */
    @SerialName("current")
    data object Current : TerminateAuthorizationRequest()
}