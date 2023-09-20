package io.timemates.backend.rsocket.features.users.requests

import kotlinx.serialization.Serializable

@Serializable
data class GetUsersRequest(
    val ids: List<Long>,
)