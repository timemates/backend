package org.tomadoro.backend.application.routes.files

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.tomadoro.backend.repositories.FilesRepository
import org.tomadoro.backend.usecases.files.GetFileUseCase

fun Route.getFileStream(
    getFileUseCase: GetFileUseCase
) = get {
    val fileId = call.request.queryParameters.getOrFail("file_id")
        .let { FilesRepository.FileId(it) }

    when (val result = getFileUseCase(fileId)) {
        is GetFileUseCase.Result.Success ->
            call.respondOutputStream {
                result.inputStream.copyTo(this)
            }

        else -> call.respond(HttpStatusCode.NotFound)
    }
}