package io.timemates.backend.timers.data.db

import io.timemates.backend.exposed.suspendedTransaction
import io.timemates.backend.exposed.update
import io.timemates.backend.timers.data.db.entities.DbTimer
import io.timemates.backend.timers.data.db.tables.TimersStateTable
import io.timemates.backend.timers.data.mappers.TimersMapper
import org.jetbrains.annotations.TestOnly
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class TableTimersStateDataSource(
    private val database: Database,
    private val timersMapper: TimersMapper,
) {
    init {
        transaction(database) {
            SchemaUtils.create(TimersStateTable)
        }
    }

    suspend fun getTimerState(timerId: Long) = suspendedTransaction(database) {
        TimersStateTable.select { TimersStateTable.TIMER_ID eq timerId }
            .singleOrNull()
            ?.let(timersMapper::resultRowToTimerState)
    }

    suspend fun setTimerState(state: DbTimer.State) = suspendedTransaction(database) {
        val condition = Op.build { TimersStateTable.TIMER_ID eq state.timerId }
        val exists = TimersStateTable.select(condition).empty()

        if (exists) {
            TimersStateTable.update(condition) {
                it[PHASE] = state.phase
                it[ENDS_AT] = state.endsAt
            }
        } else {
            TimersStateTable.insert {
                it[TIMER_ID] = state.timerId
                it[PHASE] = state.phase
                it[ENDS_AT] = state.endsAt
            }
        }
    }

    suspend fun removeTimerState(timerId: Long) = suspendedTransaction(database) {
        TimersStateTable.deleteWhere { TIMER_ID eq timerId } != 0
    }

    /**
     * Sets [DbTimer.State.Phase.OFFLINE] to all timers states that
     * becomes before [beforeTime] time.
     */
    suspend fun resetOutdatedTimerStates(
        beforeTime: Long,
    ): Unit = suspendedTransaction(database) {
        TimersStateTable.update({
            (TimersStateTable.ENDS_AT lessEq beforeTime)
                .and(TimersStateTable.PHASE neq DbTimer.State.Phase.OFFLINE)
        }) {
            it[PHASE] = DbTimer.State.Phase.OFFLINE
        }
    }

    suspend fun getTimerStates(
        ids: List<Long>,
    ): Map<Long, DbTimer.State> = suspendedTransaction(database) {
        TimersStateTable.select { TimersStateTable.TIMER_ID inList ids }
            .associate {
                val state = timersMapper.resultRowToTimerState(it)
                it[TimersStateTable.TIMER_ID] to state
            }
    }

    @TestOnly
    suspend fun clear(): Unit = suspendedTransaction(database) {
        TimersStateTable.deleteAll()
    }
}