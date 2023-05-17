package io.timemates.backend.data.authorization.db.entities

import kotlinx.serialization.Serializable

@Serializable
data class AuthorizationPageToken(
    val lastReceivedAuthorizationId: Int,
)