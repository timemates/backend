package io.timemates.backend.timers.fsm

import io.timemates.backend.fsm.StateMachine
import io.timemates.backend.fsm.StateStorage
import io.timemates.backend.timers.types.TimerEvent
import io.timemates.backend.timers.types.value.TimerId

/**
 * State machine with states of timers.
 */
typealias TimersStateMachine = StateMachine<TimerId, TimerEvent, TimerState>

/**
 * Storage with states of timers.
 */
typealias TimersStateStorage = StateStorage<TimerId, TimerState, TimerEvent>