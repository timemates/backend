package io.timemates.backend.integrations.postgresql.repositories.datasource.tables

import io.timemates.backend.types.value.UnixTime
import io.timemates.backend.repositories.AuthorizationsRepository
import io.timemates.backend.repositories.UsersRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

object AuthorizationsTable : Table("authorizations") {
    private val USER_ID = integer("user_id").references(UsersTable.USER_ID)
    private val ACCESS_TOKEN = varchar("access_token", 512)
    private val REFRESH_TOKEN = varchar("refresh_token", 512)
    private val ACCESS_TOKEN_EXPIRES_AT = long("access_token_expires_at")

    fun insert(authorization: Authorization) = insert {
        it[USER_ID] = authorization.userId.int
        it[ACCESS_TOKEN] = authorization.accessToken.string
        it[ACCESS_TOKEN_EXPIRES_AT] = authorization.expiresAt.long
        it[REFRESH_TOKEN] = authorization.refreshToken.string
    }

    fun select(
        accessToken: AuthorizationsRepository.AccessToken,
        currentTime: UnixTime
    ): Authorization? =
        select {
            ACCESS_TOKEN eq accessToken.string and
                (ACCESS_TOKEN_EXPIRES_AT greater currentTime.long)
        }.singleOrNull()?.toAuthorization()

    fun update(
        refreshToken: AuthorizationsRepository.RefreshToken,
        accessToken: AuthorizationsRepository.AccessToken,
        expiresAt: UnixTime
    ) = update({ REFRESH_TOKEN eq refreshToken.string }) {
        it[ACCESS_TOKEN] = accessToken.string
        it[ACCESS_TOKEN_EXPIRES_AT] = expiresAt.long
    }

    fun selectAll(userId: UsersRepository.UserId): List<Authorization> =
        select { USER_ID eq userId.int }.map { it.toAuthorization() }

    fun remove(accessToken: AuthorizationsRepository.AccessToken) =
        deleteWhere { ACCESS_TOKEN eq accessToken.string }

    class Authorization(
        val userId: UsersRepository.UserId,
        val accessToken: AuthorizationsRepository.AccessToken,
        val refreshToken: AuthorizationsRepository.RefreshToken,
        val expiresAt: UnixTime
    )

    private fun ResultRow.toAuthorization() = Authorization(
        UsersRepository.UserId(get(USER_ID)),
        AuthorizationsRepository.AccessToken(get(ACCESS_TOKEN)),
        AuthorizationsRepository.RefreshToken(get(REFRESH_TOKEN)),
        UnixTime(get(ACCESS_TOKEN_EXPIRES_AT))
    )
}