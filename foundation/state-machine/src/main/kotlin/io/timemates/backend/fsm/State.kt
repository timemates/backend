package io.timemates.backend.fsm

import com.timemates.backend.time.UnixTime
import kotlin.time.Duration

/**
 * An abstract class representing a state in a finite state machine.
 * @param Event the type of the events that this state can handle.
 *
 * **Should implement equals & hashCode**
 */
public interface State<out Event> {

    /**
     * The duration that this state should remain alive.
     */
    public val alive: Duration

    /**
     * When state was present.
     */
    public val publishTime: UnixTime

    /**
     * The identifier of the state.
     *
     * @see StateMachineController
     */
    public val key: Key<*>

    public interface Key<out T : State<*>>
//
//    /**
//     * Handles an event and returns the next state.
//     * @param event the event to handle.
//     * @return the next state or current if no need to change.
//     */
//    public open suspend fun onEvent(event: Event): State<Event> = this
//
//    /**
//     * Called when entering this state, before any events are processed.
//     * Can be used to perform any setup or initialization that is required
//     * before processing events.
//     * @return the current state by default or the next / transformed state.
//     */
//    public open suspend fun onEnter(): State<Event> = this
//
//    /**
//     * Called when the alive duration for this state has elapsed.
//     * Can be used to transition to a new state if required.
//     *
//     * @return the next state, or null to finish [StateMachine].
//     */
//    public open suspend fun onTimeout(): State<Event>? = null
}