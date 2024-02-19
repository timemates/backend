package org.timemates.backend.types.auth

import com.timemates.backend.time.UnixTime
import org.timemates.backend.types.auth.metadata.ClientMetadata
import org.timemates.backend.types.auth.value.Attempts
import org.timemates.backend.types.auth.value.VerificationCode
import org.timemates.backend.types.users.value.EmailAddress

data class Verification(
    val emailAddress: EmailAddress,
    val code: VerificationCode,
    val attempts: Attempts,
    val time: UnixTime,
    val isConfirmed: Boolean,
    val clientMetadata: ClientMetadata,
)