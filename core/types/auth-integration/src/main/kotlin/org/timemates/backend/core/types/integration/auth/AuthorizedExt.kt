package org.timemates.backend.core.types.integration.auth

import org.timemates.backend.foundation.authorization.Authorized
import org.timemates.backend.types.users.value.UserId
import org.timemates.backend.validation.annotations.ValidationDelicateApi
import org.timemates.backend.validation.createUnsafe

@OptIn(ValidationDelicateApi::class)
val Authorized<*>.userId: UserId get() = UserId.createUnsafe(id.long)