package io.timemates.backend.tests.database

import io.timemates.backend.application.repositories.DbUsersRepository
import io.timemates.backend.integrations.cache.storage.UsersCacheDataSource
import io.timemates.backend.integrations.postgresql.repositories.datasource.DbUsersDatabaseDataSource
import io.timemates.backend.repositories.EmailsRepository
import io.timemates.backend.repositories.UsersRepository.User.Patch
import io.timemates.backend.types.value.ShortBio
import io.timemates.backend.types.value.UnixTime
import io.timemates.backend.types.value.UserName
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import kotlin.test.Test

class UsersRepositoryTest {
    private val database = Database.connect(
        "jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;", "org.h2.Driver"
    )
    private val usersRepository = DbUsersRepository(
        DbUsersDatabaseDataSource(database),
        UsersCacheDataSource(0)
    )

    @Test
    fun testUserCreationAndGet(): Unit = runBlocking {
        val id = usersRepository.createUser(
            EmailsRepository.EmailAddress("test@example.com"),
            UserName("test"),
            ShortBio("Short test bio"),
            UnixTime(System.currentTimeMillis())
        )
        assert(usersRepository.getUser(id) != null)
    }

    @Test
    fun testUserEdit(): Unit = runBlocking {
        val id = usersRepository.createUser(
            EmailsRepository.EmailAddress("test@example.com"),
            UserName("test"),
            ShortBio("Short test bio"),
            UnixTime(System.currentTimeMillis())
        )

        usersRepository.edit(id, Patch(shortBio = ShortBio("")))

        assert(usersRepository.getUser(id)!!.shortBio == ShortBio(""))
    }

    @Test
    fun testGetUsers(): Unit = runBlocking {
        val id1 = usersRepository.createUser(
            EmailsRepository.EmailAddress("test@example.com"),
            UserName("test"),
            ShortBio("Short test bio"),
            UnixTime(System.currentTimeMillis())
        )
        val id2 = usersRepository.createUser(
            EmailsRepository.EmailAddress("test@example.com"),
            UserName("test"),
            ShortBio("Short test bio"),
            UnixTime(System.currentTimeMillis())
        )

        assert(usersRepository.getUsers(listOf(id1, id2)).size == 2)
    }

}