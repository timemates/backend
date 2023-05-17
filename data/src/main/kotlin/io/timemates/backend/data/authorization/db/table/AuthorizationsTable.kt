package io.timemates.backend.data.authorization.db.table

import io.timemates.backend.authorization.types.value.AccessHash
import io.timemates.backend.data.authorization.db.entities.DbAuthorization
import io.timemates.backend.data.authorization.db.entities.DbAuthorization.Permissions.GrantLevel
import io.timemates.backend.data.users.datasource.PostgresqlUsersDataSource
import org.jetbrains.exposed.sql.Table

object AuthorizationsTable : Table("authorizations") {
    val AUTHORIZATION_ID = integer("authorization_id").autoIncrement()
    val USER_ID = long("user_id").references(PostgresqlUsersDataSource.UsersTable.USER_ID)
    val ACCESS_TOKEN = varchar("access_token", AccessHash.SIZE)
    val REFRESH_TOKEN = varchar("refresh_token", AccessHash.SIZE)
    val EXPIRES_AT = long("access_token_expires_at")
    val CREATION_TIME = long("creation_time")

    val AUTHORIZATIONS_PERMISSION = enumeration<GrantLevel>("authorizations_permission")
        .default(GrantLevel.NOT_GRANTED)
    val TIMERS_PERMISSION = enumeration<GrantLevel>("timers_permission")
        .default(GrantLevel.NOT_GRANTED)
    val USERS_PERMISSION = enumeration<GrantLevel>("users_permission")
        .default(GrantLevel.NOT_GRANTED)
    val FILES_PERMISSION = enumeration<GrantLevel>("files_permission")
        .default(GrantLevel.NOT_GRANTED)

    override val primaryKey: PrimaryKey = PrimaryKey(USER_ID, AUTHORIZATION_ID)
}