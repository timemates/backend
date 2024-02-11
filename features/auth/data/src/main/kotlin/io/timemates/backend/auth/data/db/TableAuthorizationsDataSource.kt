package io.timemates.backend.auth.data.db

import io.timemates.backend.auth.data.db.entities.AuthorizationPageToken
import io.timemates.backend.auth.data.db.entities.DbAuthorization
import io.timemates.backend.auth.data.db.mapper.DbAuthorizationsMapper
import io.timemates.backend.auth.data.db.table.AuthorizationsTable
import io.timemates.backend.exposed.suspendedTransaction
import io.timemates.backend.exposed.update
import io.timemates.backend.pagination.Ordering
import io.timemates.backend.pagination.Page
import io.timemates.backend.pagination.PageToken
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.annotations.TestOnly
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
            val pageInfo: AuthorizationPageToken? = pageToken?.forInternal()?.let(json::decodeFromString)

            val result = AuthorizationsTable.select {
                AuthorizationsTable.USER_ID eq userId and (
                    AuthorizationsTable.AUTHORIZATION_ID greater (pageInfo?.lastReceivedAuthorizationId ?: 0))
            }
                .orderBy(AuthorizationsTable.AUTHORIZATION_ID, SortOrder.ASC)
                .limit(20)
                .map(mapper::resultRowToDbAuthorization)

            val lastId = result.lastOrNull()?.authorizationId
            val nextPageToken = if (lastId != null)
                PageToken.toGive(json.encodeToString(AuthorizationPageToken(lastId)))
            else null

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
                (AuthorizationsTable.EXPIRES_AT greater currentTime)
        }.singleOrNull()?.let(mapper::resultRowToDbAuthorization)
    }

    suspend fun removeAuthorization(accessHash: String): Boolean = suspendedTransaction(database) {
        AuthorizationsTable.deleteWhere { ACCESS_TOKEN eq accessHash } > 0
    }

    suspend fun createAuthorization(
        userId: Long,
        accessHash: String,
        refreshAccessHash: String,
        permissions: DbAuthorization.Permissions,
        expiresAt: Long,
        createdAt: Long,
        metaClientName: String,
        metaClientVersion: Double,
        metaClientIpAddress: String,
    ): Int = suspendedTransaction(database) {
        AuthorizationsTable.insert {
            it[USER_ID] = userId
            it[ACCESS_TOKEN] = accessHash
            it[REFRESH_TOKEN] = refreshAccessHash
            it[EXPIRES_AT] = expiresAt
            it[CREATION_TIME] = createdAt
            it[META_CLIENT_NAME] = metaClientName
            it[META_CLIENT_VERSION] = metaClientVersion
            it[META_CLIENT_IP_ADDRESS] = metaClientIpAddress
            it[AUTHORIZATIONS_PERMISSION] = permissions.authorization
            it[USERS_PERMISSION] = permissions.users
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

    @TestOnly
    suspend fun clearAll(): Unit = suspendedTransaction(database) {
        AuthorizationsTable.deleteAll()
    }
}