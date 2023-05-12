package io.timemates.backend.fsm

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * An interface representing a state machine.
 * @param KeyType the type of the keys used to identify individual state machines.
 */
public interface StateMachine<KeyType : Any, EventType : Any, StateType : State<EventType>> {
    /**
     * Sets the state of the specified state machine.
     * @param key the key identifying the state machine.
     * @param state the new state for the state machine.
     */
    public suspend fun setState(key: KeyType, state: StateType)

    /**
     * Sends an event to the state machine identified by a [KeyType].
     *
     * @param key the key that identifies the state machine.
     * @param event the event to be sent.
     *
     * @return [Boolean] true if the event was sent, false otherwise.
     */
    public suspend fun sendEvent(key: KeyType, event: EventType): Boolean

    /**
     * Returns a flow of the states for the specified state machine.
     * @param key the key identifying the state machine.
     * @return a flow of the states for the state machine.
     */
    public suspend fun getState(key: KeyType): Flow<StateType>?
}

/**
 * Returns current state of the specified by [key] state machine.
 */
public suspend fun <KT : Any, ET : Any, ST : State<ET>> StateMachine<KT, ET, ST>.getCurrentState(
    key: KT
): ST? {
    return getState(key)?.first()
}