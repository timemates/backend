package org.tomadoro.backend.application.routes.files

import io.ktor.server.routing.*
import org.tomadoro.backend.usecases.files.GetFileUseCase

fun Route.filesRoot(
    getFileUseCase: GetFileUseCase
) = route("files") {
    getFileStream(getFileUseCase)
}