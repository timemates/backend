package io.timemates.backend.timers.domain.types

import io.timemates.backend.types.timers.value.TimerId

/**
 * Class that represents the update of the timer.
 * It can be whether adding, removing, editing or state change.
 */
sealed class TimerUpdate {
    /**
     * Timer update that represents the adding new timer
     * to user's list.
     *
     * It can be either newly created timer or joined
     * from another & current client.
     */
    data class Add(val timer: Timer) : TimerUpdate()

    /**
     * Timer update that represents the removing of a timer
     * from user's list.
     *
     * It can be either removing from another & current client
     * or kick.
     */
    data class Remove(val timerId: TimerId) : TimerUpdate()


    /**
     * Timer update that represents the state update of a timer.
     */
    data class State(val timerId: TimerId, val newState: TimerState) : TimerUpdate()

    /**
     * Timer update that represents the information update of a timer.
     *
     * It includes settings, name and description.
     */
    data class Information(val timer: Timer) : TimerUpdate()
}