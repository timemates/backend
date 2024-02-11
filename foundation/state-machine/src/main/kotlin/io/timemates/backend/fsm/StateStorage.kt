package io.timemates.backend.fsm

/**
 * An interface for a state storage that can save and load FSM states.
 *
 * @param Id the type of key used to identify the FSM state
 * @param FsmState the type of FSM state that will be saved and loaded
 * @param Event the type of events that the FSM state can handle
 */
public interface StateStorage<Id, FsmState : State<Event>, Event> {

    /**
     * Saves the given FSM state to the storage.
     *
     * @param key the key used to identify the FSM state
     * @param state the FSM state to save
     * @throws Exception if an error occurs while saving the state
     */
    public suspend fun save(key: Id, state: FsmState)

    /**
     * Removes the state with the given key from the storage.
     * @param key the identifier of state to remove
     *
     * @return [Boolean] whether the state was removed
     */
    public suspend fun remove(key: Id): Boolean

    /**
     * Loads the FSM state with the given key from the storage.
     *
     * @param key the key of the FSM state to load
     * @return the loaded FSM state, or null if the state with the given key does not exist
     * @throws Exception if an error occurs while loading the state
     */
    public suspend fun load(key: Id): FsmState?
}