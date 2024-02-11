package io.timemates.backend.timers.data.db.tables

import org.jetbrains.exposed.sql.Table

internal object TimersParticipantsTable : Table("timers_participants") {
    val TIMER_ID = long("timer_id").references(TimersTable.ID)
    val USER_ID = long("user_id")

    /**
     * Denotes the invitation code that was used to join the timer.
     * Used to determine who invited certain user.
     *
     * @see TimersInvitesTable
     */
    val INVITE_CODE = varchar(
        name = "invite_code",
        length = TimersInvitesTable.INVITE_CODE_LENGTH
    ).references(TimersInvitesTable.INVITE_CODE).nullable()

    /**
     * Denotes when user has joined the timer.
     */
    val JOIN_TIME = long("join_time")
}