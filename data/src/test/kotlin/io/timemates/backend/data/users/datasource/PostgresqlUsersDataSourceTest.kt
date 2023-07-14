package io.timemates.backend.data.users.datasource

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import kotlin.test.*

class PostgresqlUsersDataSourceTest {
    private val databaseUrl = "jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;"
    private val databaseDriver = "org.h2.Driver"

    private val email = "user@example.com"
    private val testUserName = "Test User"
    private val testBio = "Test Bio"
    private val currentTimeMillis = System.currentTimeMillis()

    private val database = Database.connect(databaseUrl, databaseDriver)
    private val datasource = PostgresqlUsersDataSource(database)

    @BeforeTest
    fun beforeEach(): Unit = runBlocking {
        datasource.clear()
    }

    @Test
    fun `isUserExists should return true if user exists`() = runBlocking {
        val userId = datasource.createUser(email, testUserName, testBio, currentTimeMillis)
        val result = datasource.isUserExists(userId)
        assertTrue(result)
    }

    @Test
    fun `isUserExists should return false if user does not exist`() {
        val result = runBlocking { datasource.isUserExists(1234) }
        assertFalse(result)
    }

    @Test
    fun `getUserByEmail should return user if user with email exists`() = runBlocking {
        val expectedUser = PostgresqlUsersDataSource.User(
            id = datasource.createUser(email, testUserName, testBio, currentTimeMillis),
            userName = testUserName,
            userEmail = email,
            userShortDesc = testBio,
            userAvatarFileId = null,
            userGravatarId = null,
        )

        val result = datasource.getUserByEmail(email)
        assertEquals(expectedUser, result)
    }

    @Test
    fun `getUserByEmail should return null if user with email does not exist`() {
        val result = runBlocking { datasource.getUserByEmail("nonexistent@example.com") }
        assertNull(result)
    }

    @Test
    fun `getUser should return user if user with id exists`() = runBlocking {
        val userId = datasource.createUser(email, testUserName, testBio, currentTimeMillis)
        val expectedUser = PostgresqlUsersDataSource.User(
            id = userId,
            userName = testUserName,
            userEmail = email,
            userShortDesc = testBio,
            userAvatarFileId = null,
            userGravatarId = null,
        )
        val result = datasource.getUser(userId)
        assertEquals(
            expected = expectedUser,
            actual = result
        )
    }

    @Test
    fun `getUser should return null if user with id does not exist`() {
        val result = runBlocking { datasource.getUser(1234) }
        assertNull(result)
    }

    @Test
    fun `getUsers should return list of users for existing ids`() = runBlocking {
        val userId1 = datasource.createUser(email, "Test User 1", "Test Bio 1", currentTimeMillis)
        val userId2 = datasource.createUser("user5@example.com", "Test User 2", "Test Bio 2", currentTimeMillis)
        val expectedUsers = mapOf(
            userId1 to PostgresqlUsersDataSource.User(
                id = userId1,
                userName = "Test User 1",
                userEmail = email,
                userShortDesc = "Test Bio 1",
                userAvatarFileId = null,
                userGravatarId = null,
            ),
            userId2 to PostgresqlUsersDataSource.User(
                id = userId2,
                userName = "Test User 2",
                userEmail = "user5@example.com",
                userShortDesc = "Test Bio 2",
                userAvatarFileId = null,
                userGravatarId = null,
            )
        )
        val result = datasource.getUsers(listOf(userId1, userId2))
        assertEquals(expectedUsers, result)
    }
}