package org.timemates.api.rsocket.internal

/**
 * Returns null if the input string is empty or contains only whitespace characters.
 *
 * @return The input string if it is not empty or does not contain only whitespace characters, otherwise null.
 */
internal fun String.nullIfEmpty(): String? = takeIf { it.isNotBlank() }