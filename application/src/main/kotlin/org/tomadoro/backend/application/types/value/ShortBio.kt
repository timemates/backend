package org.tomadoro.backend.application.types.value

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class ShortBio(val string: String)

fun org.tomadoro.backend.domain.ShortBio.serializable() = ShortBio(string)
fun ShortBio.internal() = org.tomadoro.backend.domain.ShortBio(string)