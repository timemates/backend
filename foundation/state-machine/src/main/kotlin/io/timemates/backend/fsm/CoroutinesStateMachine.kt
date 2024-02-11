package io.timemates.backend.fsm

import com.timemates.backend.time.TimeProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.minutes


public class CoroutinesStateMachine<IdType : Any, EventType : Any, StateType : State<EventType>> internal constructor(
    private val storage: StateStorage<IdType, StateType, EventType>? = null,
    private val timeProvider: TimeProvider,
    private val controller: StateMachineController<IdType, EventType, StateType>,
    private val coroutineScope: CoroutineScope,
) : StateMachine<IdType, EventType, StateType> {
    /**
     * A concurrent hash map that holds the current state of each state machine identified by a [IdType].
     */
    private val states: ConcurrentHashMap<IdType, SharedFlow<StateType>> = ConcurrentHashMap()

    /**
     * A concurrent hash map that holds the shared flow of events for each state machine identified by a [IdType].
     */
    private val events: ConcurrentHashMap<IdType, Channel<EventType>> = ConcurrentHashMap()

    /**
     * Sends an event to the state machine identified by a [IdType].
     *
     * @param key the key that identifies the state machine.
     * @param event the event to be sent.
     */
    override suspend fun sendEvent(key: IdType, event: EventType): Boolean {
        getState(key).first() // we initialize state if it wasn't initialized before
        events[key]?.send(event) ?: return false
        return true
    }

    /**
     * Gets the state flow of the state machine identified by a [IdType].
     *
     * @param id the key that identifies the state machine.
     * @return a [Flow] that emits the current state of the state machine.
     */
    override suspend fun getState(id: IdType): SharedFlow<StateType> {
        return states.getOrPut(id) {
            channelFlow {
                val initial = controller.initial?.invoke(id)
                    ?: storage?.load(id)
                    ?: error("Couldn't get initial state – initial and storage are not implemented")

                val currentState = MutableStateFlow(initial)
                var currentTimeOutJob: Job? = null

                events[id] = Channel()

                launch {
                    currentState.withIndex().collectLatest { (index, update) ->
                        send(update)

                        currentTimeOutJob?.cancel()
                        currentTimeOutJob = launch {
                            delay(timeProvider.provide() - update.publishTime + update.alive)

                            if (update == currentState.value)
                                controller[update].onTimeout?.invoke(id, update)
                                    ?.let { currentState.value = it }
                                // by convention, if onTimeout of the state is not provided, and
                                // it reaches timeout – it's removed from the storage and channelFlow
                                // is closed
                                    ?: run {
                                        storage?.remove(id)
                                        close()
                                    }
                        }

                        if (index > 0)
                            storage?.save(id, update)
                    }
                }

                launch {
                    events[id]?.consumeEach { event ->
                        currentState.update { state ->
                            controller[state].onEvent?.invoke(id, state, event) ?: state
                        }
                    }
                }

                invokeOnClose {
                    currentTimeOutJob?.cancel()
                    states.remove(id)
                    events.compute(id) { id, channel ->
                        channel?.close()
                        null
                    }
                }
            }.shareIn(coroutineScope, started = SharingStarted.WhileSubscribed(5.minutes), 1)
        }
    }
}
