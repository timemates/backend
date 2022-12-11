package org.tomadoro.backend.repositories.integration.datasource

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.tomadoro.backend.repositories.integration.tables.TimerParticipantsTable
import org.tomadoro.backend.repositories.integration.tables.TimersTable
import kotlin.sequences.Sequence

class TimersDatabaseDataSource(
    private val database: Database
) {

    init {
        transaction(database) {
            SchemaUtils.create(TimerParticipantsTable, TimersTable)
        }
    }

    suspend fun getUserTimers(
        id: Int,
        fromTimerId: Int,
        count: Int
    ): Sequence<Timer> = newSuspendedTransaction(db = database) {
        val userTimers = TimerParticipantsTable
            .select { TimerParticipantsTable.PARTICIPANT_ID eq id }
            .map { it[TimerParticipantsTable.PARTICIPANT_ID] }

        val timers = TimersTable.select {
            TimersTable.TIMER_ID greater fromTimerId and
                (TimersTable.TIMER_ID inList userTimers)
        }.limit(count).asSequence()

//        val participantsCount = timers.map {
//            TimerParticipantsTable
//                .selectParticipantsOf(it[TimersTable.TIMER_ID])
//                .count().toInt()
//        }.toList()

        timers.map {
            it.toTimer()
        }.toList().asSequence()
    }

    suspend fun getTimerById(id: Int): Timer? = newSuspendedTransaction(db = database) {

        TimersTable.select {
            TimersTable.TIMER_ID eq id
        }.singleOrNull()?.toTimer()
    }

    suspend fun removeMember(userId: Int, timerId: Int) =
        newSuspendedTransaction(db = database) {
            TimerParticipantsTable.deleteWhere {
                TimerParticipantsTable.PARTICIPANT_ID eq userId and
                    (TimerParticipantsTable.TIMER_ID eq timerId)
            }
        }

    suspend fun removeTimer(timerId: Int) = newSuspendedTransaction(db = database) {
        TimersTable.deleteWhere { TimersTable.TIMER_ID eq timerId }
    }

    suspend fun getSettings(timerId: Int) = newSuspendedTransaction(db = database) {
        TimersTable.select { TimersTable.TIMER_ID eq timerId }
            .singleOrNull()?.let {
                it.toSettings()
            }
    }

    suspend fun setNewSettings(
        timerId: Int,
        patch: Timer.Settings.Patchable
    ) = newSuspendedTransaction(db = database) {
        TimersTable.update({ TimersTable.TIMER_ID eq timerId }) {
            if (patch.workTime != null)
                it[WORK_TIME] = patch.workTime
            if (patch.bigRestTime != null)
                it[BIG_REST_TIME] = patch.bigRestTime
            if (patch.bigRestPer != null)
                it[BIG_REST_PER] = patch.bigRestPer
            if (patch.bigRestEnabled != null)
                it[BIG_REST_TIME_ENABLED] = patch.bigRestEnabled
            if (patch.restTime != null)
                it[REST_TIME] = patch.restTime
            if (patch.isEveryoneCanPause != null)
                it[IS_EVERYONE_CAN_PAUSE] = patch.isEveryoneCanPause
            if (patch.isConfirmationRequired != null)
                it[IS_CONFIRMATION_REQUIRED] = patch.isConfirmationRequired
            if (patch.isNotesEnabled != null)
                it[IS_NOTES_ENABLED] = patch.isNotesEnabled
        }
    }

    suspend fun addMember(
        timerId: Int,
        participantId: Int
    ): Unit = newSuspendedTransaction(db = database) {
        TimerParticipantsTable.insert {
            it[TIMER_ID] = timerId
            it[PARTICIPANT_ID] = participantId
        }
    }

    suspend fun getMembersIds(
        timerId: Int,
        fromMemberId: Int,
        count: Int
    ): Sequence<Int> {
        return newSuspendedTransaction(db = database) {
            TimerParticipantsTable.select {
                TimerParticipantsTable.PARTICIPANT_ID greater fromMemberId and (
                    TimerParticipantsTable.TIMER_ID eq timerId)
            }
                .orderBy(TimerParticipantsTable.PARTICIPANT_ID, SortOrder.ASC)
                .limit(count)
                .map { it[TimerParticipantsTable.PARTICIPANT_ID] }
                .asSequence()
        }
    }

    suspend fun isMemberOf(timerId: Int, userId: Int): Boolean =
        newSuspendedTransaction(db = database) {
            TimerParticipantsTable.select {
                TimerParticipantsTable.TIMER_ID eq timerId and (TimerParticipantsTable.PARTICIPANT_ID eq userId)
            }.any()
        }

    suspend fun createTimer(
        name: String,
        creationTime: Long,
        ownerId: Int,
        settings: Timer.Settings
    ) = newSuspendedTransaction(db = database) {
        val id = TimersTable.insert {
            it[TIMER_NAME] = name
            it[CREATION_TIME] = creationTime
            it[OWNER_ID] = ownerId
            it[WORK_TIME] = settings.workTime
            it[REST_TIME] = settings.restTime
            it[BIG_REST_TIME] = settings.bigRestTime
            it[BIG_REST_TIME_ENABLED] = settings.bigRestEnabled
            it[BIG_REST_PER] = settings.bigRestPer
            it[IS_EVERYONE_CAN_PAUSE] = settings.isEveryoneCanPause
            it[IS_CONFIRMATION_REQUIRED] = settings.isConfirmationRequired
            it[IS_NOTES_ENABLED] = settings.isNotesEnabled
        }.resultedValues!!.single().toTimer().id

        TimerParticipantsTable.insert {
            it[TIMER_ID] = id
            it[PARTICIPANT_ID] = ownerId
        }

        return@newSuspendedTransaction id
    }

    class Timer(
        val id: Int,
        val timerName: String,
        val settings: Settings,
        val ownerId: Int
    ) {
        class Settings(
            val workTime: Long,
            val restTime: Long,
            val bigRestTime: Long,
            val bigRestEnabled: Boolean,
            val bigRestPer: Int,
            val isEveryoneCanPause: Boolean,
            val isConfirmationRequired: Boolean,
            val isNotesEnabled: Boolean
        ) {
            class Patchable(
                val workTime: Long? = null,
                val restTime: Long? = null,
                val bigRestTime: Long? = null,
                val bigRestEnabled: Boolean? = null,
                val bigRestPer: Int? = null,
                val isEveryoneCanPause: Boolean? = null,
                val isConfirmationRequired: Boolean? = null,
                val isNotesEnabled: Boolean? = null
            )
        }
    }

    private fun ResultRow.toSettings(): Timer.Settings {
        return Timer.Settings(
            get(TimersTable.WORK_TIME),
            get(TimersTable.REST_TIME),
            get(TimersTable.BIG_REST_TIME),
            get(TimersTable.BIG_REST_TIME_ENABLED),
            get(TimersTable.BIG_REST_PER),
            get(TimersTable.IS_EVERYONE_CAN_PAUSE),
            get(TimersTable.IS_CONFIRMATION_REQUIRED),
            get(TimersTable.IS_NOTES_ENABLED)
        )
    }

    private fun ResultRow.toTimer(): Timer {
        return Timer(
            get(TimersTable.TIMER_ID),
            get(TimersTable.TIMER_NAME),
            toSettings(),
            get(TimersTable.OWNER_ID)
        )
    }
}