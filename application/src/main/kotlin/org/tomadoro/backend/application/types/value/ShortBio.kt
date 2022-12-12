package org.tomadoro.backend.application.types.value

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class ShortBio(val string: String)

fun org.tomadoro.backend.domain.value.ShortBio.serializable() = ShortBio(string)
fun ShortBio.internal() = org.tomadoro.backend.domain.value.ShortBio(string)