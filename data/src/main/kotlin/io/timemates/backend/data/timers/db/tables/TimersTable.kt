package io.timemates.backend.data.timers.db.tables

import io.timemates.backend.data.users.datasource.PostgresqlUsersDataSource
import io.timemates.backend.exposed.emptyAsDefault
import org.jetbrains.exposed.sql.Table

internal object TimersTable : Table("timers") {
    val ID = long("id").autoIncrement()
    val NAME = varchar("name", 50)
    val DESCRIPTION = varchar("description", 200).emptyAsDefault()
    val OWNER_ID = long("owner_id")
        .references(PostgresqlUsersDataSource.UsersTable.USER_ID)
    val CREATION_TIME = long("creation_time")

    // Settings
    val WORK_TIME = long("work_time")
    val REST_TIME = long("rest_time")
    val BIG_REST_TIME = long("big_rest_time")
    val BIG_REST_TIME_ENABLED = bool("big_rest_time_enabled")
    val BIG_REST_PER = integer("big_rest_per")
    val IS_EVERYONE_CAN_PAUSE = bool("is_everyone_can_pause")
    val IS_CONFIRMATION_REQUIRED = bool("is_confirmation_required")
}