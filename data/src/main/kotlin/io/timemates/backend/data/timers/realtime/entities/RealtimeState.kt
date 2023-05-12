package io.timemates.backend.data.timers.realtime.entities

class RealtimeState(
    val timerId: Long,
    val phase: Phase,
    val endsAt: Long?,
    val publishTime: Long,
) {
    enum class Phase {
        RUNNING, PAUSED, ATTENDANCE_CONFIRMATION, OFFLINE, REST,
    }
}