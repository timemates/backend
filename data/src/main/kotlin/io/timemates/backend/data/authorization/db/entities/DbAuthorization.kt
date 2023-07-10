package io.timemates.backend.data.authorization.db.entities

data class DbAuthorization(
    val authorizationId: Int,
    val userId: Long,
    val accessHash: String,
    val refreshAccessHash: String,
    val permissions: Permissions,
    val expiresAt: Long,
    val createdAt: Long,
    val metaClientName: String,
    val metaClientVersion: String,
    val metaClientIpAddress: String,
) {
    data class Permissions(
        val authorization: GrantLevel,
        val users: GrantLevel,
        val timers: GrantLevel,
        val files: GrantLevel,
    ) {
        companion object {
            val All = Permissions(
                authorization = GrantLevel.WRITE,
                users = GrantLevel.WRITE,
                timers = GrantLevel.WRITE,
                files = GrantLevel.WRITE,
            )
        }

        enum class GrantLevel {
            READ,
            WRITE,
            NOT_GRANTED,
        }
    }
}