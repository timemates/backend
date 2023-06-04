package io.timemates.backend.configuration.settings.users

import io.timemates.backend.configuration.annotations.TimeMatesDsl

/**
 * This interface represents the configuration scope for user features.
 * It provides a property to control whether new users can register or not.
 *
 * @since 1.0
 */
@TimeMatesDsl
public interface UsersFeaturesScope {

    /**
     * Determines whether new users can register or not.
     * Set this property to `true` to allow registration, or `false` to disable it.
     *
     * ```kotlin
     * canRegister = true
     * ```
     *
     * `true` by default
     *
     * @since 1.0
     */
    public var canRegister: Boolean
}
