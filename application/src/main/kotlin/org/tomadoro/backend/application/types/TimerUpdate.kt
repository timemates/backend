package org.tomadoro.backend.application.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.types.value.Milliseconds
import org.tomadoro.backend.application.types.value.serializable
import org.tomadoro.backend.repositories.NotesRepository
import org.tomadoro.backend.repositories.SessionsRepository
import org.tomadoro.backend.repositories.UsersRepository

sealed interface TimerUpdate {
    @SerialName("session_timer_confirmation")
    @Serializable
    object Confirmation : TimerUpdate

    @SerialName("timer_settings_update")
    @Serializable
    class Settings(
        @SerialName("new_settings") val patch: TimerSettings.Patch
    ) : TimerUpdate

    @SerialName("timer_started")
    @Serializable
    class TimerStarted(@SerialName("ends_at") val endsAt: Milliseconds) : TimerUpdate

    @SerialName("timer_stopped")
    @Serializable
    class TimerStopped(@SerialName("starts_at") val startsAt: Milliseconds?) : TimerUpdate

    @Serializable
    @SerialName("user_has_joined")
    class UserHasJoined(val user: User) : TimerUpdate

    @Serializable
    @SerialName("user_has_left")
    class UserHasLeft(val user: User) : TimerUpdate

    @Serializable
    @SerialName("user_sent_note")
    class NewNote(val note: Note) : TimerUpdate

    sealed interface SessionFinished : TimerUpdate {
        /**
         * Marks that user's authorization is invalid
         * or expired.
         */
        @SerialName("client_unauthorized")
        object Unauthorized : SessionFinished

        /**
         * Marks that client has incompatible version for
         * enabled features.
         */
        @SerialName("client_outdated")
        object ClientIsTooOld : SessionFinished

        /**
         * Marks that request is missing some fields, or
         * they're invalid.
         */
        @SerialName("client_bad_request")
        object BadRequest : SessionFinished
    }
}

fun SessionsRepository.Update.serializable(): TimerUpdate {
    return when (this) {
        is SessionsRepository.Update.Confirmation ->
            TimerUpdate.Confirmation
        is SessionsRepository.Update.TimerStarted ->
            TimerUpdate.TimerStarted(endsAt.serializable())
        is SessionsRepository.Update.TimerStopped ->
            TimerUpdate.TimerStopped(startsAt?.serializable())
        is SessionsRepository.Update.Settings ->
            TimerUpdate.Settings(newSettings.serializable())
        is SessionsRepository.Update.NewNote -> TimerUpdate.NewNote(note.serializable())
        is SessionsRepository.Update.UserHasJoined ->
            TimerUpdate.UserHasJoined(user.serializable())
        is SessionsRepository.Update.UserHasLeft ->
            TimerUpdate.UserHasLeft(user.serializable())
        is SessionsRepository.Update.SessionFinished -> serializable()
    }
}

fun SessionsRepository.Update.SessionFinished.serializable(): TimerUpdate.SessionFinished {
    return when(this) {
        is SessionsRepository.Update.SessionFinished.ClientIsTooOld ->
            TimerUpdate.SessionFinished.ClientIsTooOld
        is SessionsRepository.Update.SessionFinished.BadRequest ->
            TimerUpdate.SessionFinished.BadRequest
    }
}