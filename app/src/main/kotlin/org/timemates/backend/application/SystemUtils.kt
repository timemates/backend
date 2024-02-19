package org.timemates.backend.application

internal fun getEnvOrThrow(name: String): String {
    return System.getenv(name)
        ?: error("Environment variable '$name' is not defined in the running scope.")
}