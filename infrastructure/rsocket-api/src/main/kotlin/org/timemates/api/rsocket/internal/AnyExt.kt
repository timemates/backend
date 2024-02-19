package org.timemates.api.rsocket.internal

import io.rsocket.kotlin.RSocketError
import io.timemates.rsproto.server.RSocketService

/**
 * Checks if the given value is not null. If the value is null, it throws a RSocketError.Invalid exception with the message "One of the parameters is null, but should be not null."
 *
 * @param T the type of the value to check
 * @return the value if it is not null
 * @throws RSocketError.Invalid if the value is null
 */
context(RSocketService)
internal fun <T> T?.assumeNotNull(): T = this
    ?: throw RSocketError.Invalid("One of the parameters is null, but should be not null.")