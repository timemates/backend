package io.timemates.backend.endpoints.files

import io.ktor.server.routing.*
import io.timemates.backend.usecases.files.GetFileUseCase

fun Route.filesRoot(
    getFileUseCase: GetFileUseCase
) = route("files") {
    getFileStream(getFileUseCase)
}