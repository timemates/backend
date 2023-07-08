package io.timemates.backend.data.users

import io.mockk.*
import io.timemates.backend.data.users.datasource.CachedUsersDataSource
import io.timemates.backend.data.users.datasource.PostgresqlUsersDataSource
import io.timemates.backend.testing.validation.createOrAssert
import io.timemates.backend.users.types.User
import io.timemates.backend.users.types.value.UserDescription
import io.timemates.backend.users.types.value.UserId
import io.timemates.backend.users.types.value.UserName
import kotlinx.coroutines.runBlocking
import timemates.backend.hashing.HashingRepository
import timemates.backend.hashing.repository.HashingRepository as HashingRepositoryContract
import kotlin.test.Test

class UsersRepositoryTest {
    private val postgresqlUsers = mockk<PostgresqlUsersDataSource>()
    private val cachedUsers = mockk<CachedUsersDataSource>()
    private val mapper = mockk<UserEntitiesMapper>(relaxed = true)
    private val hashingRepository: HashingRepositoryContract = HashingRepository()
    private val postgresqlUsersRepository = PostgresqlUsersRepository(
        postgresqlUsers, cachedUsers, hashingRepository,  mapper
    )

    @Test
    fun `isUserExists() shouldn't call for db if user exists in cached users data source`() = runBlocking {
        val userId = mockk<UserId>(relaxed = true)

        coEvery { cachedUsers.getUser(userId.long) } returns mockk()
        coEvery { postgresqlUsers.getUser(any()) } returns mockk()

        val result = postgresqlUsersRepository.isUserExists(userId)

        verify { cachedUsers.getUser(userId.long) }
        coVerify(inverse = true) { postgresqlUsers.isUserExists(any()) }
    }

    @Test
    fun `isUserExists() should call for database if no cache`() =
        runBlocking {
            val userId = UserId.createOrAssert(123L)

            coEvery { cachedUsers.getUser(userId.long) } returns null
            coEvery { postgresqlUsers.isUserExists(userId.long) } returns true

            postgresqlUsersRepository.isUserExists(userId)

            verify { cachedUsers.getUser(userId.long) }
            coVerify { postgresqlUsers.isUserExists(userId.long) }
        }

    @Test
    fun `isUserExists() shouldn't call for database if cached`() =
        runBlocking {
            val userId = mockk<UserId>(relaxed = true)

            coEvery { cachedUsers.getUser(userId.long) } returns mockk()

            val result = postgresqlUsersRepository.isUserExists(userId)

            verify { cachedUsers.getUser(userId.long) }
            coVerify(inverse = true) { postgresqlUsers.isUserExists(userId.long) }
        }

    @Test
    fun `getUser() should return user from cached users data source`() = runBlocking {
        // GIVEN
        val userId = mockk<UserId>(relaxed = true)
        val cachedUser = mockk<CachedUsersDataSource.User>(relaxed = true)
        val postgresqlUser = mockk<PostgresqlUsersDataSource.User>(relaxed = true)

        every { cachedUsers.getUser(any()) } returns cachedUser
        coEvery { postgresqlUsers.getUser(userId.long) } returns postgresqlUser
        every { mapper.toDomainUser(any(), any()) } returns mockk()

        // WHEN
        val result = postgresqlUsersRepository.getUser(userId)

        // THEN
        verify { cachedUsers.getUser(userId.long) }
        coVerify(inverse = true) { postgresqlUsers.getUser(userId.long) }
    }

    @Test
    fun `edit() should save to database and invalidate cache`() = runBlocking {
        // GIVEN
        val userId: UserId = mockk(relaxed = true)
        val userName: UserName = mockk(relaxed = true)
        val description: UserDescription = mockk(relaxed = true)

        coJustRun { postgresqlUsers.edit(any(), any()) }
        coJustRun { cachedUsers.invalidateUser(any()) }

        // WHEN
        postgresqlUsersRepository.edit(userId, User.Patch(userName, description))

        // THEN
        coVerify {
            postgresqlUsers.edit(
                any(),
                any()
            )
        }
        verify { cachedUsers.invalidateUser(any()) }
    }

    @Test
    fun `getUsers() should return users from cache and DB`() = runBlocking {
        val userId1 = UserId.createOrAssert(1)
        val userId2 = UserId.createOrAssert(2)
        val userId3 = UserId.createOrAssert(3)
        val userId4 = UserId.createOrAssert(4)

        val cachedUser1 = CachedUsersDataSource.User("User 1", null, null, "user1@example.com")
        val cachedUser2 = CachedUsersDataSource.User("User 2", null, null, "user2@example.com")
        val cachedUser3 = CachedUsersDataSource.User("User 3", null, null, "user3@example.com")

        val cachedUsersMap = mapOf(
            userId1.long to cachedUser1,
            userId2.long to cachedUser2,
            userId3.long to cachedUser3,
            userId4.long to null
        )

        val postgresqlUser = PostgresqlUsersDataSource.User(
            id = userId4.long,
            userName = "User 4",
            userEmail = "user4@example.com",
            userShortDesc = "4",
            userAvatarFileId = null,
            gravatarId = null
        )

        coEvery { cachedUsers.getUsers(any()) } returns cachedUsersMap
        coEvery { postgresqlUsers.getUsers(listOf(userId4.long)) } returns mapOf(postgresqlUser.id to postgresqlUser)
        justRun { cachedUsers.putUser(any(), any()) }
        every { mapper.toDomainUser(any(), any()) } returns mockk()


        postgresqlUsersRepository.getUsers(listOf(userId1, userId2, userId3, userId4))

        verify { cachedUsers.getUsers(listOf(userId1.long, userId2.long, userId3.long, userId4.long)) }
        coVerify { postgresqlUsers.getUsers(listOf(userId4.long)) }
        verify(exactly = 1) {
            cachedUsers.putUser(
                any(),
                any(),
            )
        }
    }
}