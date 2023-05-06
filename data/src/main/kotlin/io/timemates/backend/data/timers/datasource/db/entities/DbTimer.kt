package io.timemates.backend.data.timers.datasource.db.entities

data class DbTimer(
    val id: Long,
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
    ) {
        class Patchable(
            val workTime: Long? = null,
            val restTime: Long? = null,
            val bigRestTime: Long? = null,
            val bigRestEnabled: Boolean? = null,
            val bigRestPer: Int? = null,
            val isEveryoneCanPause: Boolean? = null,
            val isConfirmationRequired: Boolean? = null,
        )
    }

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