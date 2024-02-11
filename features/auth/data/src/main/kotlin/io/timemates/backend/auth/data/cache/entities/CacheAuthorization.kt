package io.timemates.backend.auth.data.cache.entities

import io.timemates.backend.types.auth.metadata.ClientMetadata

data class CacheAuthorization(
    val userId: Long,
    val accessHash: String,
    val refreshAccessHash: String,
    val permissions: Permissions,
    val expiresAt: Long,
    val createdAt: Long,
    val clientMetadata: ClientMetadata,
) {
    data class Permissions(
        val authorization: GrantLevel,
        val users: GrantLevel,
        val timers: GrantLevel,
    ) {
        companion object {
            val All = Permissions(
                authorization = GrantLevel.WRITE,
                users = GrantLevel.WRITE,
                timers = GrantLevel.WRITE,
            )
        }

        enum class GrantLevel {
            READ,
            WRITE,
            NOT_GRANTED,
        }
    }
}