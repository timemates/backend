package io.timemates.backend.application.types.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.timemates.backend.application.types.User
import io.timemates.backend.application.types.value.PageToken

@Serializable
sealed interface GetMembersResponse {
    @SerialName("success")
    @Serializable
    class Success(
        val list: List<User>,
        @SerialName("next_page_token")
        val nextPageToken: PageToken
    ) : GetMembersResponse

    @SerialName("no_access")
    @Serializable
    object NoAccess : GetMembersResponse
}