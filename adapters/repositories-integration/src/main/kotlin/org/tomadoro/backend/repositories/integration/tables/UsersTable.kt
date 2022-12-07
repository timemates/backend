package org.tomadoro.backend.repositories.integration.tables

import org.jetbrains.exposed.sql.Table

object UsersTable : Table() {
    val USER_ID = integer("user_id").autoIncrement()
    val USER_NAME = varchar("user_name", 50)
    val CREATION_TIME = long("creation_time")

    override val primaryKey: PrimaryKey = PrimaryKey(USER_ID)
}
