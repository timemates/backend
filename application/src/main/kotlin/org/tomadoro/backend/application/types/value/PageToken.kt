package org.tomadoro.backend.application.types.value

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class PageToken(val string: String)

fun org.tomadoro.backend.domain.value.PageToken.serializable() = PageToken(string)