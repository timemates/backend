package io.timemates.backend.auth.data.db.table

import io.timemates.backend.auth.data.db.entities.DbAuthorization.Permissions.GrantLevel
import io.timemates.backend.types.auth.value.AccessHash
import org.jetbrains.exposed.sql.Table

object AuthorizationsTable : Table("authorizations") {
    val AUTHORIZATION_ID = integer("authorization_id").autoIncrement()
    val USER_ID = long("user_id")
    val ACCESS_TOKEN = varchar("access_token", AccessHash.SIZE)
    val REFRESH_TOKEN = varchar("refresh_token", AccessHash.SIZE)
    val EXPIRES_AT = long("access_token_expires_at")
    val CREATION_TIME = long("creation_time")
    val META_CLIENT_NAME = varchar("meta_client_name", 128)
    val META_CLIENT_VERSION = double("meta_client_version")
    val META_CLIENT_IP_ADDRESS = varchar("meta_client_ip_address", 128)

    val AUTHORIZATIONS_PERMISSION = enumeration<GrantLevel>("authorizations_permission")
        .default(GrantLevel.NOT_GRANTED)
    val TIMERS_PERMISSION = enumeration<GrantLevel>("timers_permission")
        .default(GrantLevel.NOT_GRANTED)
    val USERS_PERMISSION = enumeration<GrantLevel>("users_permission")
        .default(GrantLevel.NOT_GRANTED)

    override val primaryKey: PrimaryKey = PrimaryKey(USER_ID, AUTHORIZATION_ID)
}