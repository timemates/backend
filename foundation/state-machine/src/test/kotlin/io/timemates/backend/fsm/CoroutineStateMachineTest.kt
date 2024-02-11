package io.timemates.backend.fsm

import com.timemates.backend.time.SystemTimeProvider
import com.timemates.backend.time.UnixTime
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.timemates.backend.validation.annotations.ValidationDelicateApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@OptIn(ValidationDelicateApi::class)
class CoroutineStateMachineTest {
    private sealed class TestEvent {
        data object ChangeToFoo : TestEvent()
        data object ChangeToBar : TestEvent()
    }

    private sealed class TestState : State<TestEvent> {
        data class Foo(override val publishTime: UnixTime) : TestState() {
            override val alive: Duration = 10.seconds
            override val key: State.Key<*> get() = Key

            companion object Key : State.Key<Foo>
        }

        data class Bar(override val publishTime: UnixTime) : TestState() {
            override val alive: Duration = 10.seconds
            override val key: State.Key<*> get() = Key

            companion object Key : State.Key<Bar>
        }
    }

    private val storage = mockk<StateStorage<Int, TestState, TestEvent>>(relaxed = true)
    private val timeProvider = SystemTimeProvider()

    private val stateMachineDef = stateMachineController<Int, TestEvent, TestState> {
        initial { TestState.Foo(timeProvider.provide()) }

        state(TestState.Foo) {
            onEvent { _, state, event ->
                when (event) {
                    TestEvent.ChangeToBar -> TestState.Bar(timeProvider.provide())
                    TestEvent.ChangeToFoo -> state
                }
            }

            onTimeout { _, _ ->
                TestState.Bar(timeProvider.provide())
            }
        }

        state(TestState.Bar) {
            onTimeout { _, _ ->
                TestState.Foo(timeProvider.provide())
            }
        }
    }

    @Test
    fun `test transition by time`(): Unit = runTest {
        // GIVEN
        val fsm = stateMachineDef.toStateMachine(storage, timeProvider, backgroundScope)

        // THEN
        val states = fsm.getState(0).take(2)
        assertIs<TestState.Foo>(states.first())

        testScheduler.advanceUntilIdle()
        testScheduler.runCurrent()

        assertIs<TestState.Bar>(states.last())
    }

    @Test
    fun `check mapping on event`(): Unit = runTest {
        // GIVEN
        val fsm = stateMachineDef.toStateMachine(storage, timeProvider, backgroundScope)

        // THEN
        val states = fsm.getState(0).take(2)
        fsm.sendEvent(0, TestEvent.ChangeToBar)
        assertIs<TestState.Bar>(states.last())
    }

    @Test
    fun `check fsm init on sendEvent`(): Unit = runTest {
        // GIVEN
        val fsm = stateMachineDef.toStateMachine(storage, timeProvider, backgroundScope)

        // THEN
        assert(fsm.sendEvent(0, TestEvent.ChangeToBar))
        assertIs<TestState.Bar>(fsm.getState(0).take(2).last())
    }

    @Test
    fun `check first state is not saved`(): Unit = runTest {
        // GIVEN
        val fsm = stateMachineDef.toStateMachine(storage, timeProvider, backgroundScope)

        // THEN
        fsm.getState(0).first()
        coVerify(inverse = true) { storage.save(any(), any()) }
    }

    @Test
    fun `check second state is saved`(): Unit = runTest {
        // GIVEN
        val fsm = stateMachineDef.toStateMachine(storage, timeProvider, backgroundScope)

        // THEN
        fsm.sendEvent(0, TestEvent.ChangeToBar)
        fsm.getState(0).take(2).last()
        coVerify { storage.save(0, any()) }
    }

    @Test
    fun `check that after subscription timeout resources cleared correctly`(): Unit = runTest {
        // GIVEN
        val fsm = stateMachineDef.copy(initial = null)
            .toStateMachine(storage, timeProvider, backgroundScope)

        // WHEN
        coEvery { storage.load(any()) } returns TestState.Foo(timeProvider.provide())

        // THEN
        fsm.getState(0).first()
        testScheduler.advanceTimeBy(5.minutes.inWholeMilliseconds)
        testScheduler.runCurrent()

        fsm.getState(0).first()
        // we assume that if storage loads for second time, everything was cleared
        coVerify(exactly = 2) { storage.load(any()) }
    }
}