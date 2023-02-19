package io.timemates.backend.integrations.postgresql.repositories.datasource.tables

import io.timemates.backend.repositories.UsersRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

object LinkedGoogleAccountsTable : Table() {
    private val USER_ID = integer("user_id").references(UsersTable.USER_ID)
    private val ACCOUNT_ID = long("account_id")
    private val EMAIL = varchar("account_email", 200)

    fun fromUser(userId: UsersRepository.UserId) = select {
        USER_ID eq userId.int
    }.singleOrNull()?.toLinkedGoogleAccount()

    fun insert(account: LinkedGoogleAccount) = insert {
        it[USER_ID] = account.userId.int
        it[ACCOUNT_ID] = account.accountId
        it[EMAIL] = account.email
    }

    fun getByAccountEmail(email: String) = select {
        EMAIL eq email
    }.singleOrNull()?.toLinkedGoogleAccount()

    class LinkedGoogleAccount(
        val userId: UsersRepository.UserId,
        val accountId: Long,
        val email: String
    )

    private fun ResultRow.toLinkedGoogleAccount(): LinkedGoogleAccount {
        return LinkedGoogleAccount(
            UsersRepository.UserId(get(USER_ID)),
            get(ACCOUNT_ID),
            get(EMAIL)
        )
    }
}