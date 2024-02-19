package org.timemates.backend.foundation.authorization

import org.timemates.backend.foundation.authorization.types.AuthorizedId


public class Authorized<@Suppress("unused") T : Scope> @AuthDelicateApi constructor(
    public val id: AuthorizedId,
)
