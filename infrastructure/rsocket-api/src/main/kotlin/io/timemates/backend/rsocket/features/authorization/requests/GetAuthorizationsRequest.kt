package io.timemates.backend.rsocket.features.authorization.requests

import io.timemates.backend.serializable.types.authorization.SerializableAuthorization
import kotlinx.serialization.Serializable

@Serializable
data class GetAuthorizationsRequest(
    val pageToken: String? = null,
) {
    @Serializable
    data class Result(val list: List<SerializableAuthorization>, val nextPageToken: String?)
}