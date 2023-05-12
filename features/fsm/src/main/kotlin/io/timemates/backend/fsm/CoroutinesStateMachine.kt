package io.timemates.backend.fsm

import com.timemates.backend.time.TimeProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

public fun <KeyType : Any, EventType : Any, StateType : State<EventType>> CoroutinesStateMachine(
    coroutineScope: CoroutineScope,
    timeProvider: TimeProvider,
    storage: StateStorage<KeyType, StateType, EventType>? = null,
): CoroutinesStateMachine<KeyType, EventType, StateType> = with(coroutineScope) {
    CoroutinesStateMachine(storage, timeProvider)
}

/**
 * Implementation of [StateMachine] using [kotlinx.coroutines] with ability to save states
 * to providable [storage]. By default, it's only saving to memory.
 *
 * @param KeyType the type of the keys that identify each state machine.
 * @param EventType the type of the events that are processed by the state machine.
 */
context (CoroutineScope)
public class CoroutinesStateMachine<KeyType : Any, EventType : Any, StateType : State<EventType>>(
    private val storage: StateStorage<KeyType, StateType, EventType>? = null,
    private val timeProvider: TimeProvider,
) : StateMachine<KeyType, EventType, StateType> {

    /**
     * A concurrent hash map that holds the current state of each state machine identified by a [KeyType].
     */
    private val states: ConcurrentHashMap<KeyType, MutableStateFlow<StateType>> = ConcurrentHashMap()

    /**
     * A concurrent hash map that holds the shared flow of events for each state machine identified by a [KeyType].
     */
    private val events: ConcurrentHashMap<KeyType, MutableSharedFlow<EventType>> = ConcurrentHashMap()

    /**
     * A concurrent hash map that holds the [Mutex] for each state machine identified by a [KeyType].
     */
    private val mutexes = ConcurrentHashMap<KeyType, Mutex>()

    /**
     * Sets the state of the state machine identified by a [KeyType].
     *
     * @param key the key that identifies the state machine.
     * @param state the new state to be set.
     */
    override suspend fun setState(key: KeyType, state: StateType) {
        // if state is the same, we should ignore upcoming state
        if (states[key] == state)
            return

        // check whether other state is present, if so, we cancel and remove it from
        // queue and set current state
        if (states[key] == null) {
            createStateAsync(key, state, true)
        } else {
            // we do not remove state on-purpose, as we will
            // reuse object on next iteration
            /* states.remove(key) */
            events.remove(key)
            mutexes.remove(key)

            setState(key, state)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun createStateAsync(key: KeyType, state: StateType, saveAtFirst: Boolean) {
        val mutex = mutexes.computeIfAbsent(key) { Mutex() }
        launch {
            mutex.withLock {
                // If the state machine does not exist, create it
                val transformedState = state.onEnter()
                // probably, there was different state before, so we keep subscribers
                // notified and reuse state flow
                val flow = states[key] ?: MutableStateFlow(transformedState as StateType)

                if(saveAtFirst)
                    storage?.save(key, state)

                // Store the CoroutineScope, state flow, and event flow in
                // the respective hash maps.
                states[key] = flow
                events[key] = MutableSharedFlow()

                // Launch a coroutine to collect events and send it to current state.
                launch {
                    events[key]?.collectLatest { event ->
                        transformedState.onEvent(event)
                            .takeIf { it != flow.value }
                            ?.let { it as StateType }
                            ?.also {
                                flow.value = it
                                storage?.save(key, state)
                            }
                    }
                }

                with(flow) {
                    // Launch a coroutine to handle the state's timeout.
                    launch {
                        delay(timeProvider.provide() - transformedState.publishTime + transformedState.alive)
                        transformedState.onTimeout()?.let { emit(it as StateType) } ?: run {
                            // If the state machine has timed out and there's no
                            // pending states, remove it from the hash maps.
                            cancel()
                            states.remove(key)
                            events.remove(key)
                            mutexes.remove(key)
                        }
                    }
                }
            }
        }
    }

    /**
     * Sends an event to the state machine identified by a [KeyType].
     *
     * @param key the key that identifies the state machine.
     * @param event the event to be sent.
     */
    override suspend fun sendEvent(key: KeyType, event: EventType): Boolean {
        events[key]?.emit(event) ?: return false

        return true
    }

    /**
     * Gets the state flow of the state machine identified by a [KeyType].
     *
     * @param key the key that identifies the state machine.
     * @return a [Flow] that emits the current state of the state machine.
     */
    override suspend fun getState(key: KeyType): Flow<StateType>? {
        states[key]?.let { state -> return state }
        storage?.load(key)?.let { state -> createStateAsync(key, state, false) }

        return states[key]
    }
}