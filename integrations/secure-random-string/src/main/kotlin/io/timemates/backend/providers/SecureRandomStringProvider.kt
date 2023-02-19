package io.timemates.backend.providers

import java.security.SecureRandom
import kotlin.random.asKotlinRandom

open class SecureRandomStringProvider(
    private val secureRandom: SecureRandom = SecureRandom()
) : RandomStringProvider {

    companion object : SecureRandomStringProvider()

    private val alphabet: List<Char> =
        ('a'..'z') + ('A'..'Z') + ('0'..'9')

    override fun provide(size: Int): String {
        return CharArray(size) {
            alphabet.random(secureRandom.asKotlinRandom())
        }.joinToString("")
    }
}