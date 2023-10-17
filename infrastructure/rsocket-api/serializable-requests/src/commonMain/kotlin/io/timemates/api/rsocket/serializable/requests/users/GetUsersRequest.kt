package io.timemates.api.rsocket.serializable.requests.users

import io.timemates.api.rsocket.serializable.types.users.SerializableUser
import kotlinx.serialization.Serializable

@Serializable
data class GetUsersRequest(
    val ids: List<Long>,
) {
    @Serializable
    data class Result(val list: List<SerializableUser>)
}