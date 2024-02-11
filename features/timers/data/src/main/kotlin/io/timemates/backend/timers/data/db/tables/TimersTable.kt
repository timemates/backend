package io.timemates.backend.timers.data.db.tables

import io.timemates.backend.exposed.emptyAsDefault
import org.jetbrains.exposed.sql.Table
import kotlin.time.Duration.Companion.minutes

internal object TimersTable : Table("timers") {
    val ID = long("id").autoIncrement()
    val NAME = varchar("name", 50)
    val DESCRIPTION = varchar("description", 200).emptyAsDefault()
    val OWNER_ID = long("owner_id")
    val CREATION_TIME = long("creation_time")

    // Settings
    val WORK_TIME = long("work_time").default(25.minutes.inWholeMilliseconds)
    val REST_TIME = long("rest_time").default(5.minutes.inWholeMilliseconds)
    val BIG_REST_TIME = long("big_rest_time").default(10.minutes.inWholeMilliseconds)
    val BIG_REST_TIME_ENABLED = bool("big_rest_time_enabled").default(false)
    val BIG_REST_PER = integer("big_rest_per").default(4)
    val IS_EVERYONE_CAN_PAUSE = bool("is_everyone_can_pause").default(false)
    val IS_CONFIRMATION_REQUIRED = bool("is_confirmation_required").default(true)

    override val primaryKey = PrimaryKey(ID)
}