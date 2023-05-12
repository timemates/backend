package io.timemates.backend.data.timers.cache.entities

data class CachedTimer(
    val name: String,
    val description: String,
    val ownerId: Long,
    val settings: Settings,
) {
    class Settings(
        val workTime: Long,
        val restTime: Long,
        val bigRestTime: Long,
        val bigRestEnabled: Boolean,
        val bigRestPer: Int,
        val isEveryoneCanPause: Boolean,
        val isConfirmationRequired: Boolean,
    )

    class State(
        val timerId: Long,
        val phase: Phase,
        val endsAt: Long?,
    ) {
        enum class Phase {
            RUNNING, PAUSED, ATTENDANCE_CONFIRMATION, OFFLINE, REST,
        }
    }
}