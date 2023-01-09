package io.timemates.backend.application.types.responses

import io.timemates.backend.application.types.Authorization
import io.timemates.backend.application.types.value.AccessToken
import io.timemates.backend.application.types.value.RefreshToken
import io.timemates.backend.application.types.value.UserId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class VerifyEmailAuthorizationResponse {
    @Serializable
    sealed class Success : VerifyEmailAuthorizationResponse() {
        @Serializable
        @SerialName("new_account")
        object NewAccount : Success()
        @Serializable
        @SerialName("account_exists")
        class ExistsAccount(val authorization: Authorization)
    }

    @Serializable
    @SerialName("attempts_exceed")
    object AttemptsExceed : VerifyEmailAuthorizationResponse()

    @Serializable
    @SerialName("attempt_failed")
    object AttemptFailed : VerifyEmailAuthorizationResponse()

    @Serializable
    @SerialName("not_found")
    object NotFound : VerifyEmailAuthorizationResponse()
}