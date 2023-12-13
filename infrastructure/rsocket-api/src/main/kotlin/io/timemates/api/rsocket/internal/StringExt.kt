package io.timemates.api.rsocket.internal

/**
 * Returns null if the input string is empty or contains only whitespace characters.
 *
 * @return The input string if it is not empty or does not contain only whitespace characters, otherwise null.
 */
internal fun String.nullIfEmpty(): String? = takeIf { it.isNotBlank() }

/**
 * Returns the original string if it is not null, or an empty string if it is null.
 *
 * @return The original string if it is not null, or an empty string.
 */
@Suppress("NOTHING_TO_INLINE")
internal inline fun String?.orEmpty(): String = this ?: ""