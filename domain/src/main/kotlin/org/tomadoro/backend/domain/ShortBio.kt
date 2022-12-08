package org.tomadoro.backend.domain

@JvmInline
value class ShortBio(val string: String) {
    init {
        require(string.length < 100) {
            "Short bio shouldn't have more than 100 symbols"
        }
    }
}