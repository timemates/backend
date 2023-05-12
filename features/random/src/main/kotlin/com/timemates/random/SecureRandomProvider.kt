package com.timemates.random

import java.security.SecureRandom
import kotlin.random.asKotlinRandom

public class SecureRandomProvider(
    secureRandom: SecureRandom = SecureRandom()
) : RandomProvider {
    private val random = secureRandom.asKotlinRandom()
    private val hashSource = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    override fun randomHash(size: Int): String {
        return (0 until size).map {
            hashSource[random.nextInt(0, hashSource.size)]
        }.joinToString("")
    }
}