package io.timemates.backend.integrations.postgresql.repositories.datasource.tables.sessions

import io.timemates.backend.integrations.postgresql.repositories.datasource.tables.TimersTable
import io.timemates.backend.integrations.postgresql.repositories.datasource.tables.UsersTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object InSessionUsersTable : IdTable<Long>("in_session_users") {
    val TIMER_ID = integer("timer_id").references(TimersTable.TIMER_ID)
    val USER_ID = integer("user_id").references(UsersTable.USER_ID)

    val LAST_ACTIVITY_TIME = long("last_activity_time")

    override val id: Column<EntityID<Long>> = long("id").autoIncrement().entityId()
}