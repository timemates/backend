package io.timemates.backend.data.authorization.db

import io.timemates.backend.data.authorization.db.entities.AuthorizationPageToken
import io.timemates.backend.data.authorization.db.entities.DbAuthorization
import io.timemates.backend.data.authorization.db.mapper.DbAuthorizationsMapper
import io.timemates.backend.data.authorization.db.table.AuthorizationsTable
import io.timemates.backend.exposed.suspendedTransaction
import io.timemates.backend.exposed.update
import io.timemates.backend.pagination.Ordering
import io.timemates.backend.pagination.PageToken
import io.timemates.backend.pagination.Page
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class TableAuthorizationsDataSource(
    private val database: Database,
    private val mapper: DbAuthorizationsMapper,
    private val json: Json = Json,
) {
    init {
        transaction(database) {
            SchemaUtils.create(AuthorizationsTable)
        }
    }

    suspend fun getAuthorizations(userId: Long, pageToken: PageToken?): Page<DbAuthorization> =
        suspendedTransaction(database) {
            val pageInfo: AuthorizationPageToken? = pageToken?.decoded()?.let(json::decodeFromString)

            val result = AuthorizationsTable.select {
                AuthorizationsTable.USER_ID eq userId and (
                    AuthorizationsTable.AUTHORIZATION_ID greater (pageInfo?.lastReceivedAuthorizationId ?: 0))
            }
                .orderBy(AuthorizationsTable.AUTHORIZATION_ID, SortOrder.ASC)
                .map(mapper::resultRowToDbAuthorization)

            val lastId = result.lastOrNull()?.authorizationId
            val nextPageToken = if (lastId != null)
                PageToken.withBase64(json.encodeToString(AuthorizationPageToken(lastId)))
            else pageToken

            return@suspendedTransaction Page(
                value = result,
                nextPageToken = nextPageToken,
                ordering = Ordering.ASCENDING,
            )
        }

    suspend fun getAuthorization(
        accessHash: String,
        currentTime: Long,
    ): DbAuthorization? = suspendedTransaction(database) {
        AuthorizationsTable.select {
            AuthorizationsTable.ACCESS_TOKEN eq accessHash and
                (AuthorizationsTable.EXPIRES_AT less currentTime)
        }.singleOrNull()?.let(mapper::resultRowToDbAuthorization)
    }

    suspend fun removeAuthorization(accessHash: String): Boolean = suspendedTransaction(database) {
        AuthorizationsTable.deleteWhere { ACCESS_TOKEN eq accessHash } > 0
    }

    suspend fun createAuthorizations(
        userId: Long,
        accessHash: String,
        refreshAccessHash: String,
        permissions: DbAuthorization.Permissions,
        expiresAt: Long,
        createdAt: Long,
    ): Int = suspendedTransaction(database) {
        AuthorizationsTable.insert {
            it[USER_ID] = userId
            it[ACCESS_TOKEN] = accessHash
            it[REFRESH_TOKEN] = refreshAccessHash
            it[EXPIRES_AT] = expiresAt
            it[CREATION_TIME] = createdAt
            it[AUTHORIZATIONS_PERMISSION] = permissions.authorization
            it[USERS_PERMISSION] = permissions.users
            it[FILES_PERMISSION] = permissions.files
            it[TIMERS_PERMISSION] = permissions.timers
        }[AuthorizationsTable.AUTHORIZATION_ID]
    }

    suspend fun renewAccessHash(
        refreshHash: String,
        newAccessHash: String,
        expiresAt: Long,
    ): Unit = suspendedTransaction(database) {
        AuthorizationsTable.update(AuthorizationsTable.REFRESH_TOKEN eq refreshHash) {
            it[ACCESS_TOKEN] = newAccessHash
            it[EXPIRES_AT] = expiresAt
        } > 0
    }
}