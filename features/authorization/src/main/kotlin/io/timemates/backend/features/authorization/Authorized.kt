package io.timemates.backend.features.authorization

import io.timemates.backend.features.authorization.types.AuthorizedId

public class Authorized(
    public val authorizedId: AuthorizedId,
    public val scopes: List<Scope>,
)