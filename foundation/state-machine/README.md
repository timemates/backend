# State Machine Library

The 'state-machine' library provides an abstraction for implementing state machines within an application. It simplifies the handling of states that change over time or as a result of user commands. The library aims to make working with state machines more straightforward and efficient.

## Purpose

The main purpose of the 'state-machine' library is to offer a structured approach to managing states in applications, particularly for scenarios where the state changes dynamically. It provides a way to represent states as objects and define their behavior when specific events occur. Additionally, the library allows for the persistence of state machines, enabling the recovery of previous states upon restarting or resuming the application.

## StateMachine Interface

The `StateMachine` interface serves as the foundation for defining and managing state machines. It is a generic interface that takes three type parameters: `KeyType`, `EventType`, and `StateType`. The `KeyType` represents the type of key used to identify individual state machines. The `EventType` represents the type of events that the state machine can process. The `StateType` represents the type of states that the state machine transitions between.

The `StateMachine` interface defines the following methods:

- `setState(key: KeyType, state: StateType)`: Sets the state of a specific state machine identified by the provided key.

- `sendEvent(key: KeyType, event: EventType): Boolean`: Sends an event to a specific state machine identified by the provided key. Returns a boolean value indicating whether the event was successfully sent.

- `getState(key: KeyType): Flow<StateType>?`: Returns a flow of states for a specific state machine identified by the provided key. The flow emits the current state of the state machine.

## State Class

The `State` abstract class represents an individual state within a finite state machine. It defines various properties and methods that determine the behavior of a state. States should implement the `equals` and `hashCode` methods to ensure proper comparison and identification.

The `State` class includes the following elements:

- `alive`: Represents the duration that the state should remain active.

- `publishTime`: Indicates when the state was initially entered.

- `onEvent(event: Event): State<Event>`: Handles an event and returns the next state based on the event's impact.

- `onEnter(): State<Event>`: Called when entering the state, before processing any events. This method can be used for setup or initialization tasks.

- `onTimeout(): State<Event>?`: Called when the alive duration for the state has elapsed. It allows transitioning to a new state or finishing the state machine.

## CoroutinesStateMachine Class

The `CoroutinesStateMachine` class is an implementation of the `StateMachine` interface that utilizes coroutines for concurrent and asynchronous processing. It provides the ability to save and load states using a `StateStorage` (optional). By default, the `CoroutinesStateMachine` saves states in memory.

The `CoroutinesStateMachine` class includes the following elements:

- `states`: A concurrent hash map that stores the current state for each state machine.

- `events`: A concurrent hash map that stores the shared flow of events for each state machine.

- `mutexes`: A concurrent hash map that holds the `Mutex` for each state machine.

- `setState(key: KeyType, state: StateType)`: Sets the state of a specific state machine identified by the provided key. It handles state transitions and manages the associated flows and coroutines.

- `sendEvent(key: KeyType, event: EventType): Boolean`: Sends an event to a specific state machine identified by the provided key. The event is emitted through the corresponding shared flow.

- `getState(key: KeyType): Flow<StateType>?`: Retrieves the flow of states for a specific state machine identified by the provided key