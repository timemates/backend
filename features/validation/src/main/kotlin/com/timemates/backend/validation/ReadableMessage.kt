package com.timemates.backend.validation

/**
 * Class that contains human-readable message of
 * validation failure.
 */
@JvmInline
public value class ReadableMessage(public val string: String) {
    /**
     * Returns [string]
     */
    override fun toString(): String {
        return string
    }
}