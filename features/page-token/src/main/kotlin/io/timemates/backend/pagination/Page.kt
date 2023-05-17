package io.timemates.backend.pagination

/**
 * A data class that represents a paginated list of items of type [T], along with a [nextPageToken].
 *
 * @param value The list of items of type [T].
 * @param nextPageToken The token used to retrieve the next page of items.
 */
public data class Page<T>(
    public val value: List<T>,
    public val nextPageToken: PageToken?,
    public val ordering: Ordering,
)

public inline fun <T, R> Page<T>.map(transform: (T) -> R): Page<R> {
    return Page(
        value.map(transform),
        nextPageToken,
        ordering,
    )
}

public inline fun <T, R> Page<T>.mapIndexed(transform: (Int, T) -> R): Page<R> {
    return Page(
        value.mapIndexed(transform),
        nextPageToken,
        ordering,
    )
}