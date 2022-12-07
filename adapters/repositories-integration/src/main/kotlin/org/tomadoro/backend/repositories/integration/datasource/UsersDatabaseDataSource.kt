package org.tomadoro.backend.repositories.integration.datasource

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.tomadoro.backend.repositories.integration.tables.UsersTable

class UsersDatabaseDataSource(
    private val database: Database
) {

    init {
        transaction(database) {
            SchemaUtils.create(UsersTable)
        }
    }

    suspend fun getUser(id: Int): User? =
        newSuspendedTransaction(db = database) {
            UsersTable.select { UsersTable.USER_ID eq id }.singleOrNull()?.toUser()
        }

    suspend fun createUser(userName: String, creationTime: Long): Int =
        newSuspendedTransaction(db = database) {
            UsersTable.insert {
                it[USER_NAME] = userName
                it[CREATION_TIME] = creationTime
            }.resultedValues!!.single().toUser().id
        }

    class User(
        val id: Int,
        val userName: String
    )

    private fun ResultRow.toUser(): User {
        return User(get(UsersTable.USER_ID), get(UsersTable.USER_NAME))
    }
}