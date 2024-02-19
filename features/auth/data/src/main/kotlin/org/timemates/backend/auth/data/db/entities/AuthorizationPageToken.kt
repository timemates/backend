package org.timemates.backend.auth.data.db.entities

import kotlinx.serialization.Serializable

@Serializable
data class AuthorizationPageToken(
    val lastReceivedAuthorizationId: Int,
)