package io.timemates.backend.endpoints.types.value

import kotlinx.serialization.Serializable
import io.timemates.backend.types.value.ShortBio as InternalShortBio

@Serializable
@JvmInline
value class ShortBio(val string: String)

fun InternalShortBio.serializable() = ShortBio(string)
fun ShortBio.internal() = InternalShortBio(string)