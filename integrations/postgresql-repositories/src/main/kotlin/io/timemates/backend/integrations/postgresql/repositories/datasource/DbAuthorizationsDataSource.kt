package io.timemates.backend.integrations.postgresql.repositories.datasource

import io.timemates.backend.types.value.UnixTime
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.integrations.postgresql.repositories.tables.AuthorizationsTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class AuthorizationsDataSource(
    private val database: Database
) {

    init {
        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(AuthorizationsTable)
        }
    }

    suspend fun create(
        userId: UsersRepository.UserId,
        accessToken: io.timemates.backend.repositories.AuthorizationsRepository.AccessToken,
        refreshToken: io.timemates.backend.repositories.AuthorizationsRepository.RefreshToken,
        expiresAt: UnixTime
    ): Unit = newSuspendedTransaction(db = database) {
        AuthorizationsTable.insert(
            AuthorizationsTable.Authorization(
                userId, accessToken, refreshToken, expiresAt
            )
        )
    }

    suspend fun remove(
        accessToken: io.timemates.backend.repositories.AuthorizationsRepository.AccessToken
    ): Boolean = newSuspendedTransaction(db = database) {
        AuthorizationsTable.remove(accessToken) > 0
    }

    suspend fun get(
        accessToken: io.timemates.backend.repositories.AuthorizationsRepository.AccessToken,
        currentTime: UnixTime
    ): AuthorizationsTable.Authorization? = newSuspendedTransaction(db = database) {
        AuthorizationsTable.select(accessToken, currentTime)
    }

    suspend fun getList(
        userId: UsersRepository.UserId
    ): List<AuthorizationsTable.Authorization> = newSuspendedTransaction(db = database) {
        AuthorizationsTable.selectAll(userId)
    }

    suspend fun renew(
        refreshToken: io.timemates.backend.repositories.AuthorizationsRepository.RefreshToken,
        accessToken: io.timemates.backend.repositories.AuthorizationsRepository.AccessToken,
        expiresAt: UnixTime
    ): AuthorizationsTable.Authorization? = newSuspendedTransaction(db = database) {
        AuthorizationsTable.update(refreshToken, accessToken, expiresAt)
        AuthorizationsTable.select(accessToken, UnixTime(0))
    }
}