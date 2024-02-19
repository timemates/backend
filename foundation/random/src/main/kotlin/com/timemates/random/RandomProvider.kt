package com.timemates.random

public interface RandomProvider {
    /**
     * Generates random string of given [size].
     */
    public fun randomHash(size: Int): String
}