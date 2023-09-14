package io.timemates.backend.rsocket.features.users

import io.timemates.backend.rsocket.internal.asPayload
import io.timemates.backend.rsocket.internal.decoding
import io.timemates.backend.rsocket.router.builders.RoutingBuilder
import io.timemates.backend.rsocket.router.builders.requestResponse
import io.timemates.backend.rsocket.features.users.requests.EditEmailRequest
import io.timemates.backend.rsocket.features.users.requests.GetUsersRequest
import io.timemates.backend.serializable.types.users.SerializableUserPatch

fun RoutingBuilder.users(
    service: RSocketUsersService,
): Unit = route("users") {
    requestResponse("email.edit") { payload ->
        payload.decoding<EditEmailRequest> { TODO() }
    }

    route("profile") {
        requestResponse("edit") { payload ->
            payload.decoding<SerializableUserPatch> { service.editUser(it).asPayload() }
        }

        requestResponse("list") { payload ->
            payload.decoding<GetUsersRequest> { service.getUsers(it.ids).asPayload() }
        }
    }
}