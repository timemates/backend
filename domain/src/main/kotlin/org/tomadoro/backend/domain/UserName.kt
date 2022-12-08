package org.tomadoro.backend.domain

@JvmInline
value class UserName(val string: String) {
    init {
        require(string.length < 50) { "User name shouldn't be more than 50 symbols" }
    }
}