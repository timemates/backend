package org.tomadoro.backend.application.types.value

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class PageToken(val string: String)