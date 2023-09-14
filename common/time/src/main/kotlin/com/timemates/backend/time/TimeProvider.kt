package com.timemates.backend.time

import io.timemates.backend.validation.markers.InternalThrowAbility

public interface TimeProvider : InternalThrowAbility {
    /**
     * Provides current time in Unix format.
     */
    public fun provide(): UnixTime
}