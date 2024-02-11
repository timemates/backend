package io.timemates.backend.auth.data.db.mapper

import io.timemates.backend.auth.data.db.entities.DbAuthorization
import io.timemates.backend.auth.data.db.table.AuthorizationsTable
import org.jetbrains.exposed.sql.ResultRow

class DbAuthorizationsMapper {
    fun resultRowToDbAuthorization(row: ResultRow): DbAuthorization {
        return DbAuthorization(
            authorizationId = row[AuthorizationsTable.AUTHORIZATION_ID],
            userId = row[AuthorizationsTable.USER_ID],
            accessHash = row[AuthorizationsTable.ACCESS_TOKEN],
            refreshAccessHash = row[AuthorizationsTable.REFRESH_TOKEN],
            expiresAt = row[AuthorizationsTable.EXPIRES_AT],
            createdAt = row[AuthorizationsTable.CREATION_TIME],
            permissions = DbAuthorization.Permissions(
                authorization = row[AuthorizationsTable.AUTHORIZATIONS_PERMISSION],
                users = row[AuthorizationsTable.USERS_PERMISSION],
                timers = row[AuthorizationsTable.TIMERS_PERMISSION],
            ),
            metaClientName = row[AuthorizationsTable.META_CLIENT_NAME],
            metaClientVersion = row[AuthorizationsTable.META_CLIENT_VERSION],
            metaClientIpAddress = row[AuthorizationsTable.META_CLIENT_IP_ADDRESS],
        )
    }
}