package org.timemates.backend.timers.data.db.tables

import org.jetbrains.exposed.sql.Table

internal object TimersInvitesTable : Table("timers_invites") {
    const val INVITE_CODE_LENGTH = 6

    // TABLE
    val TIMER_ID = long("timer_id")
    val INVITE_CODE = varchar("invite_code", INVITE_CODE_LENGTH)
    val CREATION_TIME = long("creation_time")

    /**
     * Denotes how many users can join by this invite.
     * All joined users are contained and determined by [TimersParticipantsTable.INVITE_CODE]
     */
    val MAX_JOINERS_COUNT = integer("max_joiners_count")

    val CREATOR_ID = long("creator_id")

    /**
     * Denotes whether invite code active or not.
     * It can be deleted or just expired by [MAX_JOINERS_COUNT].
     */
    val IS_ARCHIVED = bool("is_archived").default(false)

    override val primaryKey = PrimaryKey(INVITE_CODE)
}