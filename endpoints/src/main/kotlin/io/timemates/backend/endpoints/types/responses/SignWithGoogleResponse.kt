package io.timemates.backend.endpoints.types.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.timemates.backend.application.types.value.AccessToken

@Serializable
sealed interface SignWithGoogleResponse {
    @SerialName("success")
    @Serializable
    class Success(
        @SerialName("access_token") val accessToken: AccessToken
    ) : SignWithGoogleResponse
}