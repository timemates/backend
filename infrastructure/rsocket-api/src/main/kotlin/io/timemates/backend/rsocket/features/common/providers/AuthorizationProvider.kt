package io.timemates.backend.rsocket.features.common.providers

import io.timemates.backend.features.authorization.Authorized

fun interface AuthorizationProvider {
    suspend fun provide(accessHash: String): Authorized?
}
