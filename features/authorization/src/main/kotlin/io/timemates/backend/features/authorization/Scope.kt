package io.timemates.backend.features.authorization

/**
 * Represents authorization scope.
 */
public interface Scope {
    /**
     * Denotes that user has all possible permissions.
     */
    public object All : Scope
}