package io.timemates.backend.foundation.authorization

import io.timemates.backend.foundation.authorization.types.AuthorizedId


public class Authorized<@Suppress("unused") T : Scope>(
    public val id: AuthorizedId,
)
