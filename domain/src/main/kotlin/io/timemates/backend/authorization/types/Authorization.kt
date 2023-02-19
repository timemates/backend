package io.timemates.backend.authorization.types

import com.timemates.backend.time.UnixTime
import io.timemates.backend.authorization.types.value.AccessHash
import io.timemates.backend.authorization.types.value.RefreshHash
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.users.types.value.UserId

data class Authorization(
    val userId: UserId,
    val accessHash: AccessHash,
    val refreshAccessHash: RefreshHash,
    val scopes: List<Scope>,
    val expiresAt: UnixTime,
) {
    /**
     * This class represents scope of rights that
     * given to authorization.
     */
    sealed class Scope {
        /**
         * Marks that authorization can access user's public profile
         * information
         */
        data object ReadProfile : Scope()

        /**
         * Marks that authorization can access private user's profile
         * information and edit it.
         */
        data object ManageProfile : Scope()

        /**
         * Marks that authorization can access user's timers list information,
         * watch updates, etc
         */
        sealed class ReadTimers : Scope() {
            // all timers are accessible
            data object All : ReadTimers()

            // only one is accessible
            data class Single(val timerId: TimerId) : ReadTimers()
        }

        /**
         * Marks that authorization can access user's timers list information,
         * watch updates, edit timers, join timers, etc.
         */
        sealed class ManageTimers : Scope() {
            // all timers are accessible
            data object All : ManageTimers()

            // only given timer is accessible
            data class Single(val timerId: TimerId)
        }

        /**
         * Marks that authorization can access user's notifications.
         */
        data object ReadNotifications : Scope()

        /**
         * Marks that authorization has god-role.
         */
        data object All : Scope()
    }
}