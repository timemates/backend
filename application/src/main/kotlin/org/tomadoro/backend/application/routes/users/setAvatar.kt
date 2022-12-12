package org.tomadoro.backend.application.routes.users

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.jvm.javaio.*
import org.tomadoro.backend.application.plugins.authorized
import org.tomadoro.backend.application.results.SetAvatarResult
import org.tomadoro.backend.application.types.value.serializable
import org.tomadoro.backend.usecases.users.SetAvatarUseCase

fun Route.setAvatar(setAvatarUseCase: SetAvatarUseCase) = post("setAvatar") {
    authorized { userId ->
        val multipart = call.receiveMultipart()

        val filePart = multipart.readPart() as? PartData.BinaryChannelItem
            ?: return@post call.respond(HttpStatusCode.BadRequest)

        val result = setAvatarUseCase(userId, filePart.provider().toInputStream())

        call.respond<SetAvatarResult>(when(result) {
            is SetAvatarUseCase.Result.Success -> SetAvatarResult.Success(
                result.fileId.serializable()
            )
        })
    }
}