package org.tomadoro.backend.repositories.integration

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.tomadoro.backend.repositories.LinkedSocialsRepository
import org.tomadoro.backend.repositories.UsersRepository
import org.tomadoro.backend.repositories.integration.tables.LinkedGoogleAccountsTable
import org.tomadoro.backend.repositories.LinkedSocialsRepository as Contract

class LinkedSocialsRepository(
    private val database: Database
) : Contract {
    init {
        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(LinkedGoogleAccountsTable)
        }
    }

    override suspend fun getSocials(userId: UsersRepository.UserId): List<LinkedSocialsRepository.Social> {
        return newSuspendedTransaction(db = database) {
            val output = mutableListOf<LinkedSocialsRepository.Social>()
            LinkedGoogleAccountsTable.fromUser(userId)?.toExternal()
                ?.let { output.add(it) }

            return@newSuspendedTransaction output
        }
    }

    override suspend fun linkSocial(
        userId: UsersRepository.UserId,
        social: LinkedSocialsRepository.Social
    ): Unit = newSuspendedTransaction(db = database) {
        if (social is LinkedSocialsRepository.Social.Google)
            LinkedGoogleAccountsTable.insert(
                LinkedGoogleAccountsTable.LinkedGoogleAccount(
                    userId, social.accountId, social.email
                )
            )
        else error("Not supported")
    }

    override suspend fun getBySocial(
        social: LinkedSocialsRepository.Social
    ): UsersRepository.UserId? = newSuspendedTransaction(db = database) {
        return@newSuspendedTransaction when (social) {
            is LinkedSocialsRepository.Social.Google ->
                LinkedGoogleAccountsTable.getByAccountEmail(social.email)?.userId

            else -> null
        }
    }

    private fun LinkedGoogleAccountsTable.LinkedGoogleAccount.toExternal(): LinkedSocialsRepository.Social.Google {
        return LinkedSocialsRepository.Social.Google(accountId, email)
    }
}