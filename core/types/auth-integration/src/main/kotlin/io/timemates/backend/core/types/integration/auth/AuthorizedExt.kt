package io.timemates.backend.core.types.integration.auth

import io.timemates.backend.foundation.authorization.Authorized
import io.timemates.backend.types.users.value.UserId
import io.timemates.backend.validation.annotations.ValidationDelicateApi
import io.timemates.backend.validation.createUnsafe

@OptIn(ValidationDelicateApi::class)
val Authorized<*>.userId: UserId get() = UserId.createUnsafe(id.long)