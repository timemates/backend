package io.timemates.api.rsocket.serializable.requests.authorizations

import io.timemates.api.rsocket.serializable.types.authorization.SerializableAuthorization
import kotlinx.serialization.Serializable

@Serializable
data class GetAuthorizationsRequest(
    val pageToken: String? = null,
) {
    @Serializable
    data class Result(val list: List<SerializableAuthorization>, val nextPageToken: String?)
}