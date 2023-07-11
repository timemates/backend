package io.timemates.backend.authorization.types

import com.timemates.backend.time.UnixTime
import io.timemates.backend.authorization.types.metadata.ClientMetadata
import io.timemates.backend.authorization.types.value.Attempts
import io.timemates.backend.authorization.types.value.VerificationCode
import io.timemates.backend.users.types.value.EmailAddress

data class Verification(
    val emailAddress: EmailAddress,
    val code: VerificationCode,
    val attempts: Attempts,
    val time: UnixTime,
    val isConfirmed: Boolean,
    val clientMetadata: ClientMetadata
)