package org.tomadoro.backend.application.results

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.types.value.AccessToken

@Serializable
sealed interface RenewTokenResult {
    @Serializable
    @SerialName("success")
    class Success(
        @SerialName("access_token") val accessToken: AccessToken
    ) : RenewTokenResult
}