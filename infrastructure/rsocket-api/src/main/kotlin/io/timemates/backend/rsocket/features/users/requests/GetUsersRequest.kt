package io.timemates.backend.rsocket.features.users.requests

import io.timemates.backend.serializable.types.users.SerializableUser
import kotlinx.serialization.Serializable

@Serializable
data class GetUsersRequest(
    val ids: List<Long>,
) {
    @Serializable
    data class Result(val list: List<SerializableUser>)
}