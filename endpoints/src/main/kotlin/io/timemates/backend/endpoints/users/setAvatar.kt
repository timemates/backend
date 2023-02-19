package io.timemates.backend.endpoints.users

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.jvm.javaio.*
import io.timemates.backend.application.plugins.authorized
import io.timemates.backend.endpoints.types.responses.SetAvatarResponse
import io.timemates.backend.endpoints.types.value.internal
import io.timemates.backend.endpoints.types.value.serializable
import io.timemates.backend.usecases.users.SetAvatarUseCase

fun Route.setAvatar(setAvatarUseCase: SetAvatarUseCase) = post("setAvatar") {
    authorized { userId ->
        val multipart = call.receiveMultipart()

        val filePart = multipart.readPart() as? PartData.BinaryChannelItem
            ?: return@post call.respond(HttpStatusCode.BadRequest)

        val result = setAvatarUseCase(userId.internal(), filePart.provider().toInputStream())

        call.respond<SetAvatarResponse>(when(result) {
            is SetAvatarUseCase.Result.Success -> SetAvatarResponse.Success(
                result.fileId.serializable()
            )
        })
    }
}