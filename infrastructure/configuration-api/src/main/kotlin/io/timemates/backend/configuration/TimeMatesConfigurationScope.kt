package io.timemates.backend.configuration

import io.timemates.backend.configuration.annotations.TimeMatesDsl
import io.timemates.backend.configuration.database.DatabaseConfigurationScope
import io.timemates.backend.configuration.files.FilesConfigurationScope
import io.timemates.backend.configuration.mailer.MailerConfigurationScope
import io.timemates.backend.configuration.settings.TimeMatesSettingsScope

/**
 * This interface represents the configuration scope for TimeMates.
 * It provides methods to configure the mailer, database, files, and settings.
 *
 * @since 1.0
 */

@TimeMatesDsl
public interface TimeMatesConfigurationScope {

    /**
     * Configures the mailer using the specified block.
     *
     * ```kotlin
     * mailer {
     *     // Configure mailer settings here
     * }
     * ```
     *
     * @param block the block of code to configure the mailer
     * @since 1.0
     */
    public fun mailer(block: MailerConfigurationScope.() -> Unit)

    /**
     * Configures the database using the specified block.
     *
     * ```kotlin
     * database {
     *     // Configure database settings here
     * }
     * ```
     *
     * @param block the block of code to configure the database
     * @since 1.0
     */
    public fun database(block: DatabaseConfigurationScope.() -> Unit)

    /**
     * Configures the files using the specified block.
     *
     * ```kotlin
     * files {
     *     // Configure file system settings here
     * }
     * ```
     *
     * @param block the block of code to configure the files
     * @since 1.0
     */
    public fun files(block: FilesConfigurationScope.() -> Unit)

    /**
     * Configures the settings using the specified block.
     *
     * ```kotlin
     * settings {
     *     // Configure TimeMates settings here
     * }
     * ```
     *
     * @param block the block of code to configure the settings
     * @since 1.0
     */
    public fun settings(block: TimeMatesSettingsScope.() -> Unit)
}
