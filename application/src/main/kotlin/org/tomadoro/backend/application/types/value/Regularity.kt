package org.tomadoro.backend.application.types.value

import kotlinx.serialization.Serializable

/**
 * Marks regularity of an event.
 * Should always be positive.
 */
@Serializable
@JvmInline
value class Regularity(val int: Int) {
    init {
        require(int > 0) {
            "Regularity should always be positive, but got $int."
        }
    }
}