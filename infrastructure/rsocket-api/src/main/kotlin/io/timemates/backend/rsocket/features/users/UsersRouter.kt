package io.timemates.backend.rsocket.features.users

import com.y9vad9.rsocket.router.builders.RoutingBuilder
import com.y9vad9.rsocket.router.serialization.requestResponse
import io.timemates.api.rsocket.serializable.requests.users.EditEmailRequest
import io.timemates.api.rsocket.serializable.requests.users.GetUsersRequest
import io.timemates.api.rsocket.serializable.types.users.SerializableUserPatch

fun RoutingBuilder.users(
    service: RSocketUsersService,
): Unit = route("users") {
    requestResponse("email.edit") { data: EditEmailRequest ->
        TODO()
    }

    route("profile") {
        requestResponse("edit") { data: SerializableUserPatch ->
            service.editUser(data)
        }

        requestResponse("list") { data: GetUsersRequest ->
            service.getUsers(data.ids)
        }
    }
}