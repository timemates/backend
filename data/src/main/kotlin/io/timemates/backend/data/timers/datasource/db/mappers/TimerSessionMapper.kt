package io.timemates.backend.data.timers.datasource.db.mappers

import io.timemates.backend.data.timers.datasource.db.entities.DbSessionUser
import io.timemates.backend.data.timers.datasource.db.tables.TimersSessionUsersTable
import org.jetbrains.exposed.sql.ResultRow

class TimerSessionMapper {
    fun resultRowToSessionUser(resultRow: ResultRow): DbSessionUser = with(resultRow) {
        return DbSessionUser(
            timerId = get(TimersSessionUsersTable.TIMER_ID),
            userId = get(TimersSessionUsersTable.USER_ID),
            lastActivityTime = get(TimersSessionUsersTable.LAST_ACTIVITY_TIME),
        )
    }
}