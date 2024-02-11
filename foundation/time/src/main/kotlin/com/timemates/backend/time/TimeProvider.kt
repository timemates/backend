package com.timemates.backend.time

public interface TimeProvider {
    /**
     * Provides current time in Unix format.
     */
    public fun provide(): UnixTime
}