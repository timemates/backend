package io.timemates.api.rsocket.serializable.requests.authorizations

import io.timemates.api.rsocket.serializable.requests.common.RSocketRequest
import io.timemates.api.rsocket.serializable.types.authorization.SerializableAuthorization
import kotlinx.serialization.Serializable

@Serializable
data class GetAuthorizationsRequest(
    val pageToken: String? = null,
) : RSocketRequest<GetAuthorizationsRequest.Result> {
    companion object Key : RSocketRequest.Key<GetAuthorizationsRequest>

    override val key: RSocketRequest.Key<*>
        get() = Key

    @Serializable
    data class Result(val list: List<SerializableAuthorization>, val nextPageToken: String?)
}