package org.tomadoro.backend.repositories.integration.tables

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.tomadoro.backend.repositories.UsersRepository

object LinkedGoogleAccountsTable : Table() {
    val USER_ID = integer("user_id").references(UsersTable.USER_ID)
    val ACCOUNT_ID = long("account_id")
    val EMAIL = varchar("account_email", 200)

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