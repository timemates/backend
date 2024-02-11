package io.timemates.backend.types.auth

import com.timemates.backend.time.UnixTime
import io.timemates.backend.types.auth.metadata.ClientMetadata
import io.timemates.backend.types.auth.value.Attempts
import io.timemates.backend.types.auth.value.VerificationCode
import io.timemates.backend.types.users.value.EmailAddress

data class Verification(
    val emailAddress: EmailAddress,
    val code: VerificationCode,
    val attempts: Attempts,
    val time: UnixTime,
    val isConfirmed: Boolean,
    val clientMetadata: ClientMetadata,
)