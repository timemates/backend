package io.timemates.api.rsocket.serializable.requests.common

/**
 * Represents a generic RSocket request.
 *
 * @param R The type of the response for the request.
 */
interface RSocketRequest<R : Any> {
    val key: Key<*>

    /**
     * Represents a key for identifying RSocket requests.
     *
     * @param T The type parameter for the RSocket request.
     */
    interface Key<T : RSocketRequest<*>>
}