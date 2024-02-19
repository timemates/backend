package org.timemates.backend.types.auth

import com.timemates.backend.time.UnixTime
import org.timemates.backend.foundation.authorization.Scope
import org.timemates.backend.types.auth.metadata.ClientMetadata
import org.timemates.backend.types.auth.value.AccessHash
import org.timemates.backend.types.auth.value.RefreshHash
import org.timemates.backend.types.users.value.UserId

data class Authorization(
    val userId: UserId,
    val accessHash: AccessHash,
    val refreshAccessHash: RefreshHash?,
    val scopes: List<Scope>,
    val expiresAt: UnixTime,
    val createdAt: UnixTime,
    val clientMetadata: ClientMetadata,
)