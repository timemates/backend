package io.timemates.backend.repositories.datasources.types

import io.timemates.backend.types.value.UnixTime
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.repositories.UpdatesRepository
import io.timemates.backend.repositories.UsersRepository
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Update {
    abstract val expiresAt: Long?

    @SerialName("added_time")
    @Serializable
    class AddedTimer(
        val timerId: Int,
        override val expiresAt: Long?
    ) : Update()

    @SerialName("removed_timer")
    @Serializable
    class RemovedTimer(
        val timerId: Int,
        override val expiresAt: Long?
    ) : Update()

    @SerialName("settings_update")
    @Serializable
    class SettingsUpdate(
        val timerId: Int,
        val newSettings: NewSettings
    ) : Update() {
        override val expiresAt: Long? = null
    }

    @SerialName("timer_started")
    @Serializable
    class TimerStarted(
        val timerId: Int,
        override val expiresAt: Long
    ) : Update()

    @SerialName("timer_paused")
    @Serializable
    class TimerPaused(
        val timerId: Int,
        override val expiresAt: Long?
    ) : Update()

    @SerialName("timer_attendance_confirmation")
    @Serializable
    class TimerAttendanceConfirmation(
        val timerId: Int,
        override val expiresAt: Long
    ) : Update()

    @SerialName("timer_note_required")
    @Serializable
    class TimerNoteRequired(
        val timerId: Int,
        override val expiresAt: Long?
    ) : Update()

    @SerialName("timer_session_joined")
    @Serializable
    class TimerSessionJoined(
        val timerId: Int,
        val memberId: Int,
        override val expiresAt: Long?
    ) : Update()

    @SerialName("timer_session_left")
    @Serializable
    class TimerSessionLeft(
        val timerId: Int,
        val memberId: Int,
        override val expiresAt: Long?
    ) : Update()
}

internal fun UpdatesRepository.Update.internal() = when (this) {
    is UpdatesRepository.Update.AddedTimer -> Update.AddedTimer(
        timerId.int, expiresAt?.long
    )
    is UpdatesRepository.Update.RemovedTimer -> Update.RemovedTimer(
        timerId.int, expiresAt?.long
    )
    is UpdatesRepository.Update.TimerAttendanceConfirmation ->
        Update.TimerAttendanceConfirmation(timerId.int, expiresAt.long)
    is UpdatesRepository.Update.TimerNoteRequired ->
        Update.TimerNoteRequired(timerId.int, expiresAt?.long)
    is UpdatesRepository.Update.TimerPaused -> Update.TimerPaused(
        timerId.int, expiresAt?.long
    )
    is UpdatesRepository.Update.TimerSessionJoined ->
        Update.TimerPaused(timerId.int, expiresAt?.long)
    is UpdatesRepository.Update.TimerSessionLeft ->
        Update.TimerPaused(timerId.int, expiresAt?.long)
    is UpdatesRepository.Update.TimerStarted ->
        Update.TimerStarted(timerId.int, expiresAt.long)
    is UpdatesRepository.Update.SettingsUpdate ->
        Update.SettingsUpdate(timerId.int, newSettings.internal())
}

internal fun Update.external() = when(this) {
    is Update.AddedTimer ->
        UpdatesRepository.Update.AddedTimer(
            TimersRepository.TimerId(timerId), expiresAt?.let { UnixTime(it) }
        )
    is Update.RemovedTimer -> UpdatesRepository.Update.RemovedTimer(
        TimersRepository.TimerId(timerId), expiresAt?.let { UnixTime(it) }
    )
    is Update.SettingsUpdate -> UpdatesRepository.Update.SettingsUpdate(
        TimersRepository.TimerId(timerId),
        newSettings.external()
    )
    is Update.TimerAttendanceConfirmation -> UpdatesRepository.Update.TimerAttendanceConfirmation(
        TimersRepository.TimerId(timerId), UnixTime(expiresAt)
    )
    is Update.TimerNoteRequired -> UpdatesRepository.Update.TimerNoteRequired(
        TimersRepository.TimerId(timerId), expiresAt?.let { UnixTime(it) }
    )
    is Update.TimerPaused -> UpdatesRepository.Update.TimerPaused(
        TimersRepository.TimerId(timerId), expiresAt?.let { UnixTime(it) }
    )
    is Update.TimerSessionJoined -> UpdatesRepository.Update.TimerSessionJoined(
        TimersRepository.TimerId(timerId),
        UsersRepository.UserId(memberId),
        expiresAt?.let { UnixTime(it) }
    )
    is Update.TimerSessionLeft -> UpdatesRepository.Update.TimerSessionLeft(
        TimersRepository.TimerId(timerId),
        UsersRepository.UserId(memberId),
        expiresAt?.let { UnixTime(it) }
    )
    is Update.TimerStarted -> UpdatesRepository.Update.TimerStarted(
        TimersRepository.TimerId(timerId),
        UnixTime(expiresAt)
    )
}