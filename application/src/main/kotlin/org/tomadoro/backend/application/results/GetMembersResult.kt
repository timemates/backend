package org.tomadoro.backend.application.results

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.types.User
import org.tomadoro.backend.application.types.value.PageToken

@Serializable
sealed interface GetMembersResult {
    @SerialName("success")
    @Serializable
    class Success(
        val list: List<User>,
        @SerialName("next_page_token")
        val nextPageToken: PageToken
    ) : GetMembersResult

    @SerialName("no_access")
    @Serializable
    object NoAccess : GetMembersResult
}