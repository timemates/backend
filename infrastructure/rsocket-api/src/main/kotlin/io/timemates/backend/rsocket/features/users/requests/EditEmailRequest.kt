package io.timemates.backend.rsocket.features.users.requests

import kotlinx.serialization.Serializable

@Serializable
data class EditEmailRequest(
    val email: String,
)