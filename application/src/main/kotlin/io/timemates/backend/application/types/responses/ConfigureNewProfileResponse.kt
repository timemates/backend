package io.timemates.backend.application.types.responses

import io.timemates.backend.application.types.Authorization
import kotlinx.serialization.Serializable

@Serializable
sealed class ConfigureNewProfileResponse {
    @Serializable
    object NotFound : ConfigureNewProfileResponse()
    @Serializable
    class Success(
        val authorization: Authorization
    ) : ConfigureNewProfileResponse()
}