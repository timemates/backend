package org.timemates.backend.fsm

import com.timemates.backend.time.TimeProvider
import kotlinx.coroutines.CoroutineScope

public fun <IdType : Any, EventType : Any, StateType : State<EventType>> stateMachineController(
    block: StateMachineBuilder<IdType, EventType, StateType>.() -> Unit,
): StateMachineController<IdType, EventType, StateType> {
    return StateMachineBuilder<IdType, EventType, StateType>().apply(block).build()
}

@FSMDsl
public class StateMachineBuilder<IdType : Any, EventType : Any, StateType : State<EventType>> {
    private val states = mutableMapOf<State.Key<*>, StateBuilder<IdType, EventType, StateType, StateType>>()
    private var initial: (suspend (IdType) -> StateType)? = null

    public fun initial(block: suspend (IdType) -> StateType) {
        initial = block
    }

    public fun <HStateType : StateType> state(key: State.Key<HStateType>, block: StateBuilder<IdType, EventType, StateType, HStateType>.() -> Unit) {
        @Suppress("UNCHECKED_CAST")
        states += key to (StateBuilder<IdType, EventType, StateType, HStateType>().apply(block)
            as StateBuilder<IdType, EventType, StateType, StateType>)
    }

    public fun state(
        vararg keys: State.Key<StateType>,
        block: StateBuilder<IdType, EventType, StateType, StateType>.() -> Unit,
    ) {
        keys.forEach { key ->
            state(key = key, block)
        }
    }

    public class StateBuilder<IdType : Any, EventType : Any, BStateType : State<EventType>, HStateType : State<EventType>> {
        internal var _onEnter: (suspend (IdType, HStateType) -> BStateType)? = null
        internal var _onTimeout: (suspend (IdType, HStateType) -> BStateType?)? = null
        internal var _onEvent: (suspend (IdType, HStateType, EventType) -> BStateType)? = null

        public fun onEnter(block: suspend (IdType, HStateType) -> BStateType) {
            _onEnter = block
        }

        public fun onTimeout(block: suspend (IdType, HStateType) -> BStateType?) {
            _onTimeout = block
        }

        public fun onEvent(block: suspend (IdType, HStateType, EventType) -> BStateType) {
            _onEvent = block
        }
    }

    public fun build(): StateMachineController<IdType, EventType, StateType> {
        return StateMachineController(
            states.mapValues { (_, builder) ->
                StateMachineController.StateController(
                    builder._onEnter,
                    builder._onTimeout,
                    builder._onEvent,
                )
            },
            initial,
        )
    }
}

public data class StateMachineController<IdType : Any, EventType : Any, StateType : State<EventType>>(
    val states: Map<State.Key<*>, StateController<IdType, EventType, StateType>>,
    val initial: (suspend (IdType) -> StateType)?,
) {
    public operator fun <T : StateType> get(instance: T): StateController<IdType, EventType, T> {
        @Suppress("UNCHECKED_CAST")
        return states[instance.key] as StateController<IdType, EventType, T>
    }

    public data class StateController<IdType : Any, EventType : Any, StateType : State<EventType>>(
        val onEnter: (suspend (IdType, StateType) -> StateType)? = null,
        val onTimeout: (suspend (IdType, StateType) -> StateType?)? = null,
        val onEvent: (suspend (IdType, StateType, EventType) -> StateType)? = null,
    )
}

public fun <IdType : Any, EventType : Any, StateType : State<EventType>> StateMachineController<IdType, EventType, StateType>.toStateMachine(
    storage: StateStorage<IdType, StateType, EventType>,
    timeProvider: TimeProvider,
    coroutineScope: CoroutineScope,
): StateMachine<IdType, EventType, StateType> = CoroutinesStateMachine(storage, timeProvider, this, coroutineScope)
