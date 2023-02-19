package io.timemates.backend.endpoints.auth

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.timemates.backend.application.plugins.authorized
import io.timemates.backend.endpoints.types.responses.GetUserIdResponse

fun Route.getUserId() = get("getUserId") {
    authorized {
        call.respond<GetUserIdResponse>(
            GetUserIdResponse.Success(it)
        )
    }
}