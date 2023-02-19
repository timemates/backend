package io.timemates.backend.endpoints.types

import io.timemates.backend.endpoints.types.value.*
import io.timemates.backend.endpoints.types.value.Count
import io.timemates.backend.endpoints.types.value.Milliseconds
import io.timemates.backend.endpoints.types.value.Name
import io.timemates.backend.endpoints.types.value.TimerId
import io.timemates.backend.types.DetailedTimer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Timer {
    @SerialName("timer_id")
    val timerId: TimerId
    val name: Name
    @SerialName("owner_id")
    val ownerId: UserId
    val settings: TimerSettings

    @Serializable
    class Active(
        override val timerId: TimerId,
        override val name: Name,
        override val ownerId: UserId,
        override val settings: TimerSettings,
        val sessionInfo: SessionInfo
    ) : Timer {
        @Serializable
        class SessionInfo(
            @SerialName("participants_count")
            val participantsCount: Count,
            val phase: Phase,
            @SerialName("phase_ends_at")
            val phaseEndsAt: Milliseconds?
        )


        @Serializable
        enum class Phase {
            /**
             * Marks that timer currently paused.
             * It doesn't take in count pause by user.
             */
            PAUSE,

            /**
             * Marks that timer currently is running (at work time).
             */
            RUNNING,

            /**
             * Marks that timer is waiting to start, it happens if:
             * - confirmation is required, but someone didn't accept it yet.
             * - timer was paused by user, and it should be started again.
             */
            WAITING
        }
    }

    @Serializable
    class Inactive(
        override val timerId: TimerId,
        override val name: Name,
        override val ownerId: UserId,
        override val settings: TimerSettings
    ) : Timer
}

fun DetailedTimer.serializable() = when(this) {
    is DetailedTimer.Inactive ->
        serializable()
    is DetailedTimer.Active ->
        io.timemates.backend.endpoints.types.serializable()
}

fun DetailedTimer.Active.serializable() =
    Timer.Active(
        timerId.serializable(),
        name.serializable(),
        ownerId.serializable(),
        io.timemates.backend.endpoints.types.serializable(),
        io.timemates.backend.endpoints.types.serializable()
    )

fun DetailedTimer.Active.SessionInfo.serializable() =
    Timer.Active.SessionInfo(
        participantsCount.serializable(),
        io.timemates.backend.endpoints.types.serializable(),
        phaseEndsAt?.serializable()
    )

fun DetailedTimer.Inactive.serializable() =
    Timer.Inactive(
        timerId.serializable(),
        name.serializable(),
        ownerId.serializable(),
        io.timemates.backend.endpoints.types.serializable()
    )

fun DetailedTimer.Active.Phase.serializable() =
    when(this) {
        DetailedTimer.Active.Phase.RUNNING ->
            Timer.Active.Phase.RUNNING
        DetailedTimer.Active.Phase.WAITING ->
            Timer.Active.Phase.WAITING
        DetailedTimer.Active.Phase.PAUSE ->
            Timer.Active.Phase.PAUSE
    }