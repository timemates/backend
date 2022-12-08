package org.tomadoro.backend.domain

@JvmInline
value class Count(val int: Int) {
    companion object {
        val MAX = Count(Int.MAX_VALUE)
    }
}