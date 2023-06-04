package io.timemates.backend.features.authorization

public interface AuthorizedContext<S : Scope> {
    /**
     * Current user authorization. Stores user's id.
     */
    public val authorization: Authorized
}

/**
 * Creates [AuthorizedContext] with given scope.
 *
 * @param provider authorization provider of given scope. If
 * authorization does not have given [S]cope & authorization
 * is not valid, then it should return null.
 * @param onFailure invokes when [provider] returns null
 * @param block that invokes on success with provided user id.
 */
public inline fun <S : Scope, R> authorizationProvider(
    provider: () -> Authorized?,
    onFailure: () -> Nothing,
    block: context(AuthorizedContext<S>) () -> R,
): R {
    val authorized = provider() ?: onFailure()

    val scope = object : AuthorizedContext<S> {
        override val authorization: Authorized = authorized
    }

    return with(scope) {
        block(this)
    }
}

