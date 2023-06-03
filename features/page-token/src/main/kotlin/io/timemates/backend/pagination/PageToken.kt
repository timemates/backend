@file:OptIn(ExperimentalEncodingApi::class)

package io.timemates.backend.pagination

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * A value class representing a page token.
 * A page token is used to propagate pagination in APIs. It is a string token that
 * identifies a specific page of results, and is used to retrieve the next page
 * of results.
 * @param string The string representation of the page token.
 */
@JvmInline
public value class PageToken private constructor(private val string: String) {

    public fun decoded(): String = Base64.decode(string).decodeToString()

    public fun encoded(): String = string

    public companion object {
        public fun withBase64(value: String): PageToken {
            return PageToken(Base64.encode(value.encodeToByteArray()))
        }

        public fun raw(value: String): PageToken {
            return PageToken(value)
        }
    }
}