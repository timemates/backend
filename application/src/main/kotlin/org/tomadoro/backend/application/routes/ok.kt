package org.tomadoro.backend.application.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.ok() {
    get("ok") {
        call.respond("ok")
    }
}