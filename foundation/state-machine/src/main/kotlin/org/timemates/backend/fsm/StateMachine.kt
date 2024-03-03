package org.timemates.backend.fsm

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first

/**
 * An interface representing a state machine.
 * @param IdType the type of the keys used to identify individual state machines.
 */
public interface StateMachine<IdType : Any, EventType : Any, StateType : State<EventType>> {
    /**
     * Sends an event to the state machine identified by a [IdType].
     *
     * @param key the key that identifies the state machine.
     * @param event the event to be sent.
     *
     * @return [Boolean] true if the event was sent, false otherwise.
     */
    public suspend fun sendEvent(key: IdType, event: EventType): Boolean

    /**
     * Returns a flow of the states for the specified state machine.
     * @param id the identifier of the state machine.
     * @return a flow of the states for the state machine.
     */
    public suspend fun getState(id: IdType): SharedFlow<StateType>
}

/**
 * Returns current state of the specified by [key] state machine.
 */
public suspend fun <KT : Any, ET : Any, ST : State<ET>> StateMachine<KT, ET, ST>.getCurrentState(
    key: KT,
): ST {
    return getState(key).first()
}

public suspend fun <KT : Any, ET : Any, ST : State<ET>> StateMachine<KT, ET, ST>.getCurrentState(
    keys: List<KT>,
): Map<KT, ST> = coroutineScope {
    keys.associateWith { getCurrentState(it) }
}