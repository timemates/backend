package io.timemates.backend.endpoints.types.value

import io.timemates.backend.types.value.PageToken as InternalPageToken
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class PageToken(val string: String)

fun InternalPageToken.serializable() = PageToken(string)