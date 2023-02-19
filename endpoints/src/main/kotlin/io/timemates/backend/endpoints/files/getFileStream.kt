package io.timemates.backend.endpoints.files

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.timemates.backend.repositories.FilesRepository
import io.timemates.backend.usecases.files.GetFileUseCase

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