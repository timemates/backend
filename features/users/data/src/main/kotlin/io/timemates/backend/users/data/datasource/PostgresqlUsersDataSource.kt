package io.timemates.backend.users.data.datasource

import io.timemates.backend.exposed.suspendedTransaction
import org.jetbrains.annotations.TestOnly
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class PostgresqlUsersDataSource(private val database: Database) {
    internal object UsersTable : Table("users") {
        val USER_ID = long("user_id").autoIncrement()
        val USER_NAME = varchar("user_name", 50)
        val USER_EMAIL = varchar("user_email", 200)
        val USER_SHORT_DESC = varchar("user_short_desc", 100).nullable()
        val AVATAR_FILE_ID = varchar("user_avatar_id", 100).nullable()
        val CREATION_TIME = long("creation_time")
        val GRAVATAR_ID = varchar("gravatar_id", 32).nullable()

        override val primaryKey: PrimaryKey = PrimaryKey(USER_ID)
    }

    init {
        transaction(database) {
            SchemaUtils.create(UsersTable)
        }
    }

    suspend fun isUserExists(id: Long): Boolean = suspendedTransaction(database) {
        UsersTable.select { UsersTable.USER_ID eq id }.any()
    }

    suspend fun getUserByEmail(email: String) = suspendedTransaction(database) {
        UsersTable.select { UsersTable.USER_EMAIL eq email }.singleOrNull()?.toUser()
    }

    suspend fun getUser(id: Long): User? =
        suspendedTransaction(database) {
            UsersTable.select { UsersTable.USER_ID eq id }.singleOrNull()?.toUser()
        }

    suspend fun getUsers(collection: List<Long>): Map<Long, User> =
        suspendedTransaction(database) {
            UsersTable.select { UsersTable.USER_ID inList (collection) }.asSequence()
                .map { it.toUser() }.associateBy { it.id }
        }

    suspend fun edit(id: Long, patch: User.Patch): Unit =
        suspendedTransaction(database) {
            UsersTable.update({ UsersTable.USER_ID eq id }) { update ->
                patch.userName?.let { update[USER_NAME] = it }
                patch.userAvatarFileId?.let {
                    update[AVATAR_FILE_ID] = it
                    update[GRAVATAR_ID] = null
                }
                patch.userGravatarId?.let {
                    update[GRAVATAR_ID] = it
                    update[AVATAR_FILE_ID] = null
                }
                patch.userShortDesc?.let { update[USER_SHORT_DESC] = it }
            }
        }

    suspend fun createUser(email: String, userName: String, shortBio: String?, creationTime: Long): Long =
        suspendedTransaction(database) {
            UsersTable.insert {
                it[USER_EMAIL] = email
                it[USER_NAME] = userName
                it[USER_SHORT_DESC] = shortBio
                it[CREATION_TIME] = creationTime
            }.resultedValues!!.single().toUser().id
        }

    suspend fun setGravatar(id: Long, gravatarId: String) {
        suspendedTransaction(database) {
            UsersTable.update({ UsersTable.USER_ID eq id }) {
                it[GRAVATAR_ID] = gravatarId
                it[AVATAR_FILE_ID] = null
            }
        }
    }

    data class User(
        val id: Long,
        val userName: String,
        val userEmail: String,
        val userShortDesc: String?,
        val userAvatarFileId: String?,
        val userGravatarId: String?,
    ) {
        data class Patch(
            val userName: String? = null,
            val userShortDesc: String? = null,
            val userAvatarFileId: String? = null,
            val userGravatarId: String? = null,
        )
    }

    private fun ResultRow.toUser(): User {
        return User(
            get(UsersTable.USER_ID),
            get(UsersTable.USER_NAME),
            get(UsersTable.USER_EMAIL),
            get(UsersTable.USER_SHORT_DESC),
            get(UsersTable.AVATAR_FILE_ID),
            get(UsersTable.GRAVATAR_ID),
        )
    }

    @TestOnly
    suspend fun clear(): Unit = suspendedTransaction(database) {
        UsersTable.deleteAll()
    }
}