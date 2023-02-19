package io.timemates.backend.endpoints.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.timemates.backend.endpoints.types.value.Milliseconds
import io.timemates.backend.endpoints.types.value.serializable
import io.timemates.backend.repositories.SessionsRepository

//sealed interface TimerUpdate {
//    @SerialName("session_timer_confirmation")
//    @Serializable
//    object Confirmation : TimerUpdate
//
//    @SerialName("timer_settings_update")
//    @Serializable
//    class Settings(
//        @SerialName("new_settings") val patch: TimerSettings.Patch
//    ) : TimerUpdate
//
//    @SerialName("timer_started")
//    @Serializable
//    class TimerStarted(@SerialName("ends_at") val endsAt: Milliseconds) : TimerUpdate
//
//    @SerialName("timer_stopped")
//    @Serializable
//    class TimerStopped(@SerialName("starts_at") val startsAt: Milliseconds?) : TimerUpdate
//
//    @Serializable
//    @SerialName("user_has_joined")
//    class UserHasJoined(val user: User) : TimerUpdate
//
//    @Serializable
//    @SerialName("user_has_left")
//    class UserHasLeft(val user: User) : TimerUpdate
//
//    @Serializable
//    sealed interface SessionFinished : TimerUpdate {
//        /**
//         * Marks that user's authorization is invalid
//         * or expired.
//         */
//        @Serializable
//        @SerialName("client_unauthorized")
//        object Unauthorized : SessionFinished
//
//        /**
//         * Marks that client has incompatible version for
//         * enabled features.
//         */
//        @Serializable
//        @SerialName("client_outdated")
//        object ClientIsTooOld : SessionFinished
//
//        /**
//         * Marks that request is missing some fields, or
//         * they're invalid.
//         */
//        @Serializable
//        @SerialName("client_bad_request")
//        object BadRequest : SessionFinished
//
//        /**
//         * Marks that session was stopped by the server.
//         * Usually shouldn't be returned, but it reserved behaviour.
//         */
//        @Serializable
//        @SerialName("session_stopped")
//        object Stopped : SessionFinished
//    }
//}
//
//fun SessionsRepository.serializable(): TimerUpdate {
//    return when (this) {
//        is SessionsRepository.Update.Confirmation ->
//            TimerUpdate.Confirmation
//        is SessionsRepository.Update.TimerStarted ->
//            TimerUpdate.TimerStarted(endsAt.serializable())
//        is SessionsRepository.Update.TimerStopped ->
//            TimerUpdate.TimerStopped(startsAt?.serializable())
//        is SessionsRepository.Update.Settings ->
//            TimerUpdate.Settings(newSettings.serializable())
//        is SessionsRepository.Update.UserHasJoined ->
//            TimerUpdate.UserHasJoined(user.serializable())
//        is SessionsRepository.Update.UserHasLeft ->
//            TimerUpdate.UserHasLeft(user.serializable())
//        is SessionsRepository.Update.SessionFinished ->
//            TimerUpdate.SessionFinished.Stopped
//    }
//}