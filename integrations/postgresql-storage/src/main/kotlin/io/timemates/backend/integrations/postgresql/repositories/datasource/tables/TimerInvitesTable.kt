package io.timemates.backend.integrations.postgresql.repositories.datasource.tables

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

object TimerInvitesTable : Table("timer_invites") {
    private val TIMER_ID = integer("timer_id").references(TimersTable.TIMER_ID)
    private val INVITE_CODE = varchar("invite_code", 64)
    private val LIMIT = integer("limit")
    private val CREATION_TIME = long("creation_time")

    fun selectAllOf(timerId: Int) = select {
        TIMER_ID eq timerId
    }.map { it.toInvite() }

    fun delete(inviteCode: String) = deleteWhere {
        INVITE_CODE eq inviteCode
    }

    fun select(code: String): Invite? = select {
        INVITE_CODE eq code
    }.singleOrNull()?.toInvite()

    fun insert(invite: Invite) = insert {
        it[TIMER_ID] = invite.timerId
        it[INVITE_CODE] = invite.inviteCode
        it[CREATION_TIME] = invite.creationTime
        it[LIMIT] = invite.limit
    }

    fun setLimitCount(code: String, limit: Int) {
        update({ INVITE_CODE eq code }) {
            it[LIMIT] = limit
        }
    }

    fun selectCount(timerId: Int, after: Long) = select {
        TIMER_ID eq timerId and (CREATION_TIME greater after)
    }.count()

    class Invite(
        val timerId: Int,
        val inviteCode: String,
        val creationTime: Long,
        val limit: Int
    )

    private fun ResultRow.toInvite(): Invite {
        return Invite(
            get(TIMER_ID),
            get(INVITE_CODE),
            get(CREATION_TIME),
            get(LIMIT)
        )
    }
}