package io.timemates.backend.authorization.types

import com.timemates.backend.time.UnixTime
import io.timemates.backend.authorization.types.metadata.Metadata
import io.timemates.backend.authorization.types.value.AccessHash
import io.timemates.backend.authorization.types.value.RefreshHash
import io.timemates.backend.features.authorization.Scope
import io.timemates.backend.users.types.value.UserId

data class Authorization(
    val userId: UserId,
    val accessHash: AccessHash,
    val refreshAccessHash: RefreshHash,
    val scopes: List<Scope>,
    val expiresAt: UnixTime,
    val createdAt: UnixTime,
    val metadata: Metadata
)