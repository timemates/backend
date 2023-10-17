package io.timemates.api.rsocket.serializable.requests.users

import io.timemates.api.rsocket.serializable.requests.common.RSocketRequest
import io.timemates.api.rsocket.serializable.types.users.SerializableUser
import kotlinx.serialization.Serializable

@Serializable
data class GetUsersRequest(
    val ids: List<Long>,
) : RSocketRequest<GetUsersRequest.Result> {
    companion object Key : RSocketRequest.Key<GetUsersRequest>

    override val key: RSocketRequest.Key<*>
        get() = Key

    @Serializable
    data class Result(val list: List<SerializableUser>)
}