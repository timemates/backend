package io.timemates.backend.integrations.postgresql.repositories.datasource

import io.timemates.backend.integrations.postgresql.repositories.datasource.tables.UsersTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class DbUsersDatabaseDataSource(
    private val database: Database
) {

    init {
        transaction(database) {
            SchemaUtils.create(UsersTable)
        }
    }

    suspend fun getUserByEmail(email: String) = newSuspendedTransaction(db = database) {
        UsersTable.select { UsersTable.USER_EMAIL eq email }.singleOrNull()?.toUser()
    }

    suspend fun getUser(id: Int): User? =
        newSuspendedTransaction(db = database) {
            UsersTable.select { UsersTable.USER_ID eq id }.singleOrNull()?.toUser()
        }

    suspend fun getUsers(collection: Collection<Int>): Collection<User> =
        newSuspendedTransaction(db = database) {
            UsersTable.select { UsersTable.USER_ID inList (collection) }
                .map { it.toUser() }
        }

    suspend fun edit(id: Int, patch: User.Patch) =
        newSuspendedTransaction(db = database) {
            UsersTable.update({ UsersTable.USER_ID eq id }) { update ->
                patch.userName?.let { update[USER_NAME] = it }
                patch.userAvatarFileId?.let { update[AVATAR_FILE_ID] = it }
                patch.userShortDesc?.let { update[USER_SHORT_DESC] = it }
            }
        }

    suspend fun createUser(email: String, userName: String, shortBio: String?, creationTime: Long): Int =
        newSuspendedTransaction(db = database) {
            UsersTable.insert {
                it[USER_EMAIL] = email
                it[USER_NAME] = userName
                it[USER_SHORT_DESC] = shortBio
                it[CREATION_TIME] = creationTime
            }.resultedValues!!.single().toUser().id
        }

    class User(
        val id: Int,
        val userName: String,
        val userEmail: String,
        val userShortDesc: String?,
        val userAvatarFileId: String?
    ) {
        class Patch(
            val userName: String? = null,
            val userShortDesc: String? = null,
            val userAvatarFileId: String? = null
        )
    }

    private fun ResultRow.toUser(): User {
        return User(
            get(UsersTable.USER_ID),
            get(UsersTable.USER_NAME),
            get(UsersTable.USER_EMAIL),
            get(UsersTable.USER_SHORT_DESC),
            get(UsersTable.AVATAR_FILE_ID)
        )
    }
}