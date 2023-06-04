package io.timemates.backend.configuration.settings

import io.timemates.backend.configuration.annotations.TimeMatesDsl
import io.timemates.backend.configuration.settings.users.UsersFeaturesScope

/**
 * This interface represents the configuration scope for TimeMates settings.
 * It provides a method to configure user features.
 *
 * @since 1.0
 */
@TimeMatesDsl
public interface TimeMatesSettingsScope {

    /**
     * Configures user features using the specified block.
     *
     * ```kotlin
     * users {
     *     canRegister = true
     * }
     * ```
     *
     * @param block the block of code to configure user features
     * @since 1.0
     */
    public fun users(block: UsersFeaturesScope.() -> Unit)
}
