package io.timemates.backend.endpoints.types.value

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class RefreshToken(val string: String)

fun io.timemates.backend.repositories.AuthorizationsRepository.RefreshToken.serializable() = RefreshToken(string)