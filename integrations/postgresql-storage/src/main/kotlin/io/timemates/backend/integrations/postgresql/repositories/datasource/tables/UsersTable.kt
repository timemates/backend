package io.timemates.backend.integrations.postgresql.repositories.datasource.tables

import org.jetbrains.exposed.sql.Table

object UsersTable : Table("users") {
    val USER_ID = integer("user_id").autoIncrement()
    val USER_NAME = varchar("user_name", 50)
    val USER_EMAIL = varchar("user_email", 200)
    val USER_SHORT_DESC = varchar("user_short_desc", 100).nullable()
    val AVATAR_FILE_ID = varchar("user_avatar_id", 100).nullable()
    val CREATION_TIME = long("creation_time")

    override val primaryKey: PrimaryKey = PrimaryKey(USER_ID)
}
