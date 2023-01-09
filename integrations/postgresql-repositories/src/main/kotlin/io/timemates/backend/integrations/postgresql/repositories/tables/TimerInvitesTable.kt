package io.timemates.backend.integrations.postgresql.repositories.tables

import io.timemates.backend.repositories.TimerInvitesRepository
import io.timemates.backend.repositories.TimersRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import io.timemates.backend.types.value.Count

object TimerInvitesTable : Table("timer_invites") {
    private val TIMER_ID = integer("timer_id").references(TimersTable.TIMER_ID)
    private val INVITE_CODE = varchar("invite_code", 64)
    private val LIMIT = integer("limit")

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
        it[TIMER_ID] = invite.timerId.int
        it[INVITE_CODE] = invite.inviteCode.string
        it[LIMIT] = invite.limit.int
    }

    fun setLimitCount(code: String, limit: Int) {
        update({ INVITE_CODE eq code }) {
            it[LIMIT] = limit
        }
    }

    class Invite(
        val timerId: TimersRepository.TimerId,
        val inviteCode: TimerInvitesRepository.Code,
        val limit: Count
    )

    private fun ResultRow.toInvite(): Invite {
        return Invite(
            TimersRepository.TimerId(get(TIMER_ID)),
            TimerInvitesRepository.Code(get(INVITE_CODE)),
            Count(get(LIMIT))
        )
    }
}