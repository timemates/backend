package io.timemates.backend.timers.data.db.tables

import org.jetbrains.exposed.sql.Table

/**
 * Table with the list of users that are confirmed or should confirm their
 * attendance.
 *
 * We keep this information,
 */
internal object TimersSessionUsersTable : Table("timers_sessions") {
    val TIMER_ID = long("timer_id").references(TimersTable.ID)
    val USER_ID = long("user_id")

    /**
     * Denotes that the user has confirmed their attendance.
     */
    val IS_CONFIRMED = bool("is_confirmed").default(false)

    /**
     * Last user's activity time.
     *
     * Denotes when last time user has made any request to the session (PING request).
     * If the user wasn't active for 15+ minutes, he should be marked as inactive.
     */
    val LAST_ACTIVITY_TIME = long("last_activity_time").default(0)
}