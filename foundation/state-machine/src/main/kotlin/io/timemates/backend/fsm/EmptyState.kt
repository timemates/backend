package io.timemates.backend.fsm

import com.timemates.backend.time.UnixTime
import kotlin.time.Duration

/**
 * An implementation of the [State] class that represents an empty state with no input and no output.
 * This state has an infinite lifetime, and ignores all input events.
 */
public object EmptyState : State<Nothing>, State.Key<EmptyState> {
    override val alive: Duration = Duration.INFINITE
    override val publishTime: UnixTime = UnixTime.ZERO
    override val key: State.Key<*>
        get() = this
}