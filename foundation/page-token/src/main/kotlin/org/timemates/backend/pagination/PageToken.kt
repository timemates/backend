@file:OptIn(ExperimentalEncodingApi::class)

package org.timemates.backend.pagination

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

    @OptIn(ExperimentalEncodingApi::class)
    public fun forInternal(): String = Base64.decode(string).decodeToString()

    public fun forPublic(): String = string

    public companion object {

        /**
         * Converts the given value to a PageToken to be given to the consumer.
         *
         * @param value The value to convert.
         * @return The converted PageToken.
         */
        @OptIn(ExperimentalEncodingApi::class)
        public fun toGive(value: String): PageToken {
            return PageToken(Base64.encode(value.encodeToByteArray()))
        }

        /**
         * Accepts raw access token as encoded one (like from request).
         *
         * @param value The page token to be accepted.
         * @return The created PageToken object.
         */
        public fun accept(value: String): PageToken {
            return PageToken(value)
        }
    }
}