package org.tomadoro.backend.repositories.integration.tables

import org.jetbrains.exposed.sql.*
import org.tomadoro.backend.repositories.TimerInvitesRepository
import org.tomadoro.backend.repositories.TimersRepository

object TimerInvitesTable : Table() {
    val TIMER_ID = integer("timer_id").references(TimersTable.TIMER_ID)
    val INVITE_CODE = varchar("invite_code", 64)
    val LIMIT = integer("limit")

    fun selectAllOf(timerId: TimersRepository.TimerId) = select {
        TIMER_ID eq timerId.int
    }.map { it.toInvite() }

    fun delete(inviteCode: TimerInvitesRepository.Code) = deleteWhere {
        INVITE_CODE eq inviteCode.string
    }

    fun select(code: TimerInvitesRepository.Code): Invite? = select {
        INVITE_CODE eq code.string
    }.singleOrNull()?.toInvite()

    fun insert(invite: Invite) = insert {
        it[TIMER_ID] = invite.timerId.int
        it[INVITE_CODE] = invite.inviteCode.string
        it[LIMIT] = invite.limit.int
    }

    fun setLimitCount(code: TimerInvitesRepository.Code, limit: TimerInvitesRepository.Count) {
        update({ INVITE_CODE eq code.string }) {
            it[LIMIT] = limit.int
        }
    }

    class Invite(
        val timerId: TimersRepository.TimerId,
        val inviteCode: TimerInvitesRepository.Code,
        val limit: TimerInvitesRepository.Count
    )

    private fun ResultRow.toInvite(): Invite {
        return Invite(
            TimersRepository.TimerId(get(TIMER_ID)),
            TimerInvitesRepository.Code(get(INVITE_CODE)),
            TimerInvitesRepository.Count(get(LIMIT))
        )
    }
}