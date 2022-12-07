package org.tomadoro.backend.application.types.value

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class AccessToken(val string: String)

fun org.tomadoro.backend.repositories.AuthorizationsRepository.AccessToken.serializable() = AccessToken(string)