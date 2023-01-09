package io.timemates.backend.application.routes.auth

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.timemates.backend.application.types.responses.ConfigureNewProfileResponse
import io.timemates.backend.application.types.serializable
import io.timemates.backend.application.types.value.Name
import io.timemates.backend.application.types.value.ShortBio
import io.timemates.backend.application.types.value.internal
import io.timemates.backend.application.types.value.internalAsUserName
import io.timemates.backend.repositories.VerificationsRepository
import io.timemates.backend.usecases.auth.ConfigureNewAccountUseCase
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
private data class ConfigureNewProfileBody(
    @SerialName("user_name")
    val userName: Name,
    @SerialName("short_bio")
    val shortBio: ShortBio?
)

fun Route.configureNewProfile(
    configureNewAccountUseCase: ConfigureNewAccountUseCase
) = post("email/verify/configure") {
    val verificationToken = call.request.queryParameters
        .getOrFail("verification_token")
        .let { VerificationsRepository.VerificationToken(it) }
    val body = call.receive<ConfigureNewProfileBody>()

    val result = configureNewAccountUseCase(
        verificationToken, body.userName.internalAsUserName(), body.shortBio?.internal()
    )

    call.respond(
        when(result) {
            is ConfigureNewAccountUseCase.Result.Success ->
                ConfigureNewProfileResponse.Success(result.authorization.serializable())
            is ConfigureNewAccountUseCase.Result.NotFound ->
                ConfigureNewProfileResponse.NotFound
        }
    )
}