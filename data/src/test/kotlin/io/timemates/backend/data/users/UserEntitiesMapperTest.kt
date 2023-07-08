package io.timemates.backend.data.users

import io.timemates.backend.data.users.datasource.CachedUsersDataSource
import io.timemates.backend.data.users.datasource.PostgresqlUsersDataSource
import io.timemates.backend.files.types.value.FileId
import io.timemates.backend.testing.validation.createOrAssert
import io.timemates.backend.users.types.User
import io.timemates.backend.users.types.value.EmailAddress
import io.timemates.backend.users.types.value.UserDescription
import io.timemates.backend.users.types.value.UserId
import io.timemates.backend.users.types.value.UserName
import kotlin.test.Test
import kotlin.test.assertEquals

class UserEntitiesMapperTest {

    private val mapper = UserEntitiesMapper()

    @Test
    fun `toDomainUser should map to domain user correctly`() {
        val userId = 123L
        val cachedUser = CachedUsersDataSource.User(
            "John Doe",
            "Short bio",
            null,
            "johndoe@example.com"
        )

        val expectedUser = User(
            UserId.createOrAssert(userId),
            UserName.createOrAssert(cachedUser.name),
            cachedUser.email?.let { EmailAddress.createOrAssert(it) },
            cachedUser.shortBio?.let { UserDescription.createOrAssert(it) },
            cachedUser.avatarFileId?.let { FileId.createOrAssert(it) }
        )

        val actualUser = mapper.toDomainUser(userId, cachedUser)

        assertEquals(
            expected = expectedUser,
            actual = actualUser
        )
    }

    @Test
    fun `toCached should map to cached user correctly`() {
        val pUser = PostgresqlUsersDataSource.User(
            0,
            "John Doe",
            "email",
            "Short description",
            null,
            gravatarId = null
        )

        val expectedCachedUser = CachedUsersDataSource.User(
            name = pUser.userName,
            shortBio = pUser.userShortDesc,
            avatarFileId = pUser.userAvatarFileId,
            email = pUser.userEmail
        )

        val actualCachedUser = mapper.toCachedUser(pUser)

        assertEquals(
            expected = expectedCachedUser,
            actual = actualCachedUser
        )
    }

    @Test
    fun `toPostgresqlUserPatch converts User patch to PostgresqlUsersDataSource User patch`() {
        val userPatch = User.Patch(
            name = UserName.createOrAssert("John"),
            shortBio = UserDescription.createOrAssert("This is a short bio"),
            avatarId = null
        )

        val expected = PostgresqlUsersDataSource.User.Patch(
            userPatch.name?.string,
            userPatch.shortBio?.string,
            userPatch.avatarId?.string
        )
        val actual = mapper.toPostgresqlUserPatch(userPatch)

        assertEquals(expected, actual)
    }

    @Test
    fun `toCachedUser converts User to CachedUsersDataSource User`() {
        val user = User(
            id = UserId.createOrAssert(123),
            name = UserName.createOrAssert("John"),
            emailAddress = EmailAddress.createOrAssert("john@example.com"),
            description = UserDescription.createOrAssert("This is a description"),
            avatarId = null
        )

        val expected = CachedUsersDataSource.User(
            user.name.string,
            user.description?.string,
            user.avatarId?.string,
            user.emailAddress?.string
        )
        val actual = mapper.toCachedUser(user)

        assertEquals(expected, actual)
    }

    @Test
    fun `toDomainUser converts PostgresqlUsersDataSource User to domain User`() {
        val pUser = PostgresqlUsersDataSource.User(
            id = 123,
            userName = "John",
            userEmail = "john@example.com",
            userShortDesc = "This is a description",
            userAvatarFileId = null,
            gravatarId = null
        )

        val expected = User(
            id = UserId.createOrAssert(pUser.id),
            name = UserName.createOrAssert(pUser.userName),
            emailAddress = EmailAddress.createOrAssert(pUser.userEmail),
            description = pUser.userShortDesc?.let { UserDescription.createOrAssert(it) },
            avatarId = null
        )
        val actual = mapper.toDomainUser(pUser)

        assertEquals(
            expected = expected,
            actual = actual
        )
    }
}