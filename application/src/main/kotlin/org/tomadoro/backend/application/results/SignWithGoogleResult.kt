package org.tomadoro.backend.application.results

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.types.value.AccessToken

@Serializable
sealed interface SignWithGoogleResult {
    @SerialName("success")
    @Serializable
    class Success(
        @SerialName("access_token") val accessToken: AccessToken
    ) : SignWithGoogleResult
}