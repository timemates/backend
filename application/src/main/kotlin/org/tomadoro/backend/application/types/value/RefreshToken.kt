package org.tomadoro.backend.application.types.value

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class RefreshToken(val string: String)

fun org.tomadoro.backend.repositories.AuthorizationsRepository.RefreshToken.serializable() = RefreshToken(string)