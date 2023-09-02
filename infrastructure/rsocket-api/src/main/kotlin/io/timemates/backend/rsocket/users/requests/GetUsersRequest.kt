package io.timemates.backend.rsocket.users.requests

import kotlinx.serialization.Serializable

@Serializable
data class GetUsersRequest(
    val ids: List<Long>,
)