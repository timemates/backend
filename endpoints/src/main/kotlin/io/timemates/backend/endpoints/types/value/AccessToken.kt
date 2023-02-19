package io.timemates.backend.endpoints.types.value

import kotlinx.serialization.Serializable
import io.timemates.backend.repositories.AuthorizationsRepository

@Serializable
@JvmInline
value class AccessToken(val string: String)

fun AuthorizationsRepository.AccessToken.serializable() = AccessToken(string)
fun AccessToken.internal() = AuthorizationsRepository.AccessToken(string)