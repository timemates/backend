package org.tomadoro.backend.application.routes.auth

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.tomadoro.backend.application.plugins.authorized
import org.tomadoro.backend.application.results.GetUserIdResult
import org.tomadoro.backend.application.types.value.serializable

fun Route.getUserId() = get("user-id") {
    authorized {
        call.respond<GetUserIdResult>(
            GetUserIdResult.Success(it.serializable())
        )
    }
}