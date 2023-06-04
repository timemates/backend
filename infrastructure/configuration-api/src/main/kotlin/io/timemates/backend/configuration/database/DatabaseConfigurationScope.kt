package io.timemates.backend.configuration.database

import io.timemates.backend.configuration.annotations.TimeMatesDsl

/**
 * This interface represents the configuration scope for a database.
 * It provides methods for configuring the database connection.
 *
 * @since 1.0
 */
@TimeMatesDsl
public interface DatabaseConfigurationScope {

    /**
     * Configures the database to use the in-memory H2 implementation.
     * This method sets up the database connection for in-memory usage.
     *
     * ```kotlin
     * inMemory()
     * ```
     *
     * @since 1.0
     */
    public fun inMemory()

    /**
     * Configures the database connection to use PostgreSQL with the provided settings.
     * This method sets up the database connection for PostgreSQL usage.
     *
     * ```kotlin
     * postgresql {
     *     url = "jdbc:postgresql://localhost:5432/mydb"
     *     user = "username"
     *     password = "password"
     * }
     * ```
     *
     * @param block the block of code to configure the PostgreSQL settings
     * @since 1.0
     */
    public fun postgresql(block: PostgresqlScope.() -> Unit)

    /**
     * This interface represents the configuration scope for the PostgreSQL settings.
     *
     * @since 1.0
     */
    @TimeMatesDsl
    public interface PostgresqlScope {
        /**
         * The URL of the PostgreSQL database.
         *
         * @since 1.0
         */
        public var url: String

        /**
         * The username for the PostgreSQL database.
         *
         * @since 1.0
         */
        public var user: String

        /**
         * The password for the PostgreSQL database.
         *
         * @since 1.0
         */
        public var password: String
    }
}