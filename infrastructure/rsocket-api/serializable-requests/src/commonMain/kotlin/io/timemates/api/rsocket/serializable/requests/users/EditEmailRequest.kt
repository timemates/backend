package io.timemates.api.rsocket.serializable.requests.users

import kotlinx.serialization.Serializable

@Serializable
data class EditEmailRequest(
    val email: String,
)