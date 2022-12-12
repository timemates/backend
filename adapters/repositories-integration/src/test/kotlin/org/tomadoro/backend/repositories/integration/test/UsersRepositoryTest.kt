package org.tomadoro.backend.repositories.integration.test

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.platform.commons.annotation.Testable
import org.tomadoro.backend.domain.value.DateTime
import org.tomadoro.backend.domain.value.ShortBio
import org.tomadoro.backend.domain.value.UserName
import org.tomadoro.backend.repositories.integration.UsersRepository
import org.tomadoro.backend.repositories.UsersRepository.User.Patch
import org.tomadoro.backend.repositories.integration.datasource.DbUsersDatabaseDataSource

@Testable
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UsersRepositoryTest {
    private val database = Database.connect(
        "jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;", "org.h2.Driver"
    )
    private val usersRepository = UsersRepository(DbUsersDatabaseDataSource(database))

    @Test
    fun testUserCreationAndGet(): Unit = runBlocking {
        val id = usersRepository.createUser(
            UserName("test"),
            ShortBio("Short test bio"),
            DateTime(System.currentTimeMillis())
        )
        assert(usersRepository.getUser(id) != null)
    }

    @Test
    fun testUserEdit(): Unit = runBlocking {
        val id = usersRepository.createUser(
            UserName("test"),
            ShortBio("Short test bio"),
            DateTime(System.currentTimeMillis())
        )

        usersRepository.edit(id, Patch(shortBio = ShortBio("")))

        assert(usersRepository.getUser(id)!!.shortBio == ShortBio(""))
    }

    @Test
    fun testGetUsers(): Unit = runBlocking {
        val id1 = usersRepository.createUser(
            UserName("test"),
            ShortBio("Short test bio"),
            DateTime(System.currentTimeMillis())
        )
        val id2 = usersRepository.createUser(
            UserName("test"),
            ShortBio("Short test bio"),
            DateTime(System.currentTimeMillis())
        )

        assert(usersRepository.getUsers(listOf(id1, id2)).size == 2)
    }

}