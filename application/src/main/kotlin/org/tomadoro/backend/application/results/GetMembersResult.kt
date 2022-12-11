package org.tomadoro.backend.application.results

import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.types.User
import org.tomadoro.backend.application.types.value.PageToken

@Serializable
sealed interface GetMembersResult {
    @Serializable
    class Success(
        val list: List<User>,
        val nextPageToken: PageToken
    ) : GetMembersResult

    @Serializable
    object BadPageToken : GetMembersResult

    @Serializable
    object NoAccess : GetMembersResult
}