package org.tomadoro.backend.repositories.integration.datasource

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.tomadoro.backend.domain.DateTime
import org.tomadoro.backend.repositories.UsersRepository
import org.tomadoro.backend.repositories.integration.tables.AuthorizationsTable

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
        accessToken: org.tomadoro.backend.repositories.AuthorizationsRepository.AccessToken,
        refreshToken: org.tomadoro.backend.repositories.AuthorizationsRepository.RefreshToken,
        expiresAt: DateTime
    ): Unit = newSuspendedTransaction(db = database) {
        AuthorizationsTable.insert(
            AuthorizationsTable.Authorization(
                userId, accessToken, refreshToken, expiresAt
            )
        )
    }

    suspend fun remove(
        accessToken: org.tomadoro.backend.repositories.AuthorizationsRepository.AccessToken
    ): Boolean = newSuspendedTransaction(db = database) {
        AuthorizationsTable.remove(accessToken) > 0
    }

    suspend fun get(
        accessToken: org.tomadoro.backend.repositories.AuthorizationsRepository.AccessToken,
        currentTime: DateTime
    ): AuthorizationsTable.Authorization? = newSuspendedTransaction(db = database) {
        AuthorizationsTable.select(accessToken, currentTime)
    }

    suspend fun getList(
        userId: UsersRepository.UserId
    ): List<AuthorizationsTable.Authorization> = newSuspendedTransaction(db = database) {
        AuthorizationsTable.selectAll(userId)
    }

    suspend fun renew(
        refreshToken: org.tomadoro.backend.repositories.AuthorizationsRepository.RefreshToken,
        accessToken: org.tomadoro.backend.repositories.AuthorizationsRepository.AccessToken,
        expiresAt: DateTime
    ): AuthorizationsTable.Authorization? = newSuspendedTransaction(db = database) {
        AuthorizationsTable.update(refreshToken, accessToken, expiresAt)
        AuthorizationsTable.select(accessToken, DateTime(0))
    }
}