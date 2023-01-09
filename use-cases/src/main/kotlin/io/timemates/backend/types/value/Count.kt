package io.timemates.backend.types.value

@JvmInline
value class Count(val int: Int) {
    companion object {
        val MAX = Count(Int.MAX_VALUE)
    }
}