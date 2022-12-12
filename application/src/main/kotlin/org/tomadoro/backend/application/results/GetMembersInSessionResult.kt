package org.tomadoro.backend.application.results

import org.tomadoro.backend.application.types.User

sealed interface GetMembersInSessionResult {
    @JvmInline
    value class Success(val list: List<User>) : GetMembersInSessionResult
    object NoAccess : GetMembersInSessionResult
    object BadPageToken : GetMembersInSessionResult
}