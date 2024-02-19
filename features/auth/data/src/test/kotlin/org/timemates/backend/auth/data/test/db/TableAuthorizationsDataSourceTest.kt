package org.timemates.backend.auth.data.test.db

import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.Database
import org.timemates.backend.auth.data.db.TableAuthorizationsDataSource
import org.timemates.backend.auth.data.db.entities.DbAuthorization
import org.timemates.backend.auth.data.db.mapper.DbAuthorizationsMapper
import kotlin.test.*
import kotlin.time.Duration.Companion.minutes

class TableAuthorizationsDataSourceTest {
    private val databaseUrl = "jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;"
    private val databaseDriver = "org.h2.Driver"

    private val database = Database.connect(databaseUrl, databaseDriver)
    private val datasource = TableAuthorizationsDataSource(database, DbAuthorizationsMapper())

    @BeforeTest
    @AfterTest
    fun `clean table`() = runTest {
        datasource.clearAll()
    }

    @Test
    fun `check createAuthorization saves correctly`() = runTest {
        val expected = DbAuthorization(
            1,
            0,
            "test-access-hash", // for tests length doesn't matter
            "test-refresh-hash", // for tests length doesn't matter
            permissions = DbAuthorization.Permissions.All,
            expiresAt = System.currentTimeMillis() + 1.minutes.inWholeMilliseconds,
            createdAt = System.currentTimeMillis(),
            metaClientName = "test-client",
            metaClientVersion = 1.0,
            metaClientIpAddress = "localhost",
        )

        val id = datasource.createAuthorization(
            userId = expected.userId,
            accessHash = expected.accessHash,
            refreshAccessHash = expected.refreshAccessHash,
            permissions = expected.permissions,
            expiresAt = expected.expiresAt,
            createdAt = expected.createdAt,
            metaClientName = expected.metaClientName,
            metaClientVersion = expected.metaClientVersion,
            metaClientIpAddress = expected.metaClientIpAddress,
        )

        assertEquals(
            expected = expected.copy(authorizationId = id),
            actual = datasource.getAuthorizations(0, null)
                .value.firstOrNull(),
        )
    }

    @Test
    fun `check renewToken works correctly`() = runTest {
        val initial = DbAuthorization(
            1,
            0,
            "test-access-hash", // for tests length doesn't matter
            "test-refresh-hash", // for tests length doesn't matter
            permissions = DbAuthorization.Permissions.All,
            expiresAt = System.currentTimeMillis() + 1.minutes.inWholeMilliseconds,
            createdAt = System.currentTimeMillis(),
            metaClientName = "test-client",
            metaClientVersion = 1.0,
            metaClientIpAddress = "localhost",
        )

        val id = datasource.createAuthorization(
            userId = initial.userId,
            accessHash = initial.accessHash,
            refreshAccessHash = initial.refreshAccessHash,
            permissions = initial.permissions,
            expiresAt = initial.expiresAt,
            createdAt = initial.createdAt,
            metaClientName = initial.metaClientName,
            metaClientVersion = initial.metaClientVersion,
            metaClientIpAddress = initial.metaClientIpAddress,
        )

        val newAccessHash = "new-access-hash"
        val newRefreshHash = "new-refresh-hash"
        datasource.renewAccessHash(initial.refreshAccessHash, newAccessHash, newRefreshHash, Long.MAX_VALUE)

        assertEquals(
            expected = initial.copy(authorizationId = id, accessHash = newAccessHash, refreshAccessHash = newRefreshHash, expiresAt = Long.MAX_VALUE),
            actual = datasource.getAuthorizations(0, null)
                .value.firstOrNull(),
        )
    }

    @Test
    fun `check removeAuthorization works correctly`() = runTest {
        val auth = DbAuthorization(
            1,
            0,
            "test-access-hash", // for tests length doesn't matter
            "test-refresh-hash", // for tests length doesn't matter
            permissions = DbAuthorization.Permissions.All,
            expiresAt = System.currentTimeMillis() + 1.minutes.inWholeMilliseconds,
            createdAt = System.currentTimeMillis(),
            metaClientName = "test-client",
            metaClientVersion = 1.0,
            metaClientIpAddress = "localhost",
        )

        datasource.createAuthorization(
            userId = auth.userId,
            accessHash = auth.accessHash,
            refreshAccessHash = auth.refreshAccessHash,
            permissions = auth.permissions,
            expiresAt = auth.expiresAt,
            createdAt = auth.createdAt,
            metaClientName = auth.metaClientName,
            metaClientVersion = auth.metaClientVersion,
            metaClientIpAddress = auth.metaClientIpAddress,
        )

        datasource.removeAuthorization(auth.accessHash)

        assert(datasource.getAuthorizations(0, null).value.isEmpty())
    }

    @Test
    fun `check getAuthorizations works correctly`() = runTest {
        val userId = 0L

        // Create some authorization records
        val auth1 = createAuth(userId, "access-hash-1")
        val auth2 = createAuth(userId, "access-hash-2")
        val auth3 = createAuth(userId, "access-hash-3")

        // Get the first page
        val firstPage = datasource.getAuthorizations(userId, null)

        assertContentEquals(
            expected = listOf(auth1, auth2, auth3),
            actual = firstPage.value
        )

        // Get the second page
        val nextPageToken1 = firstPage.nextPageToken
        assertNotNull(nextPageToken1)
    }

    private suspend fun createAuth(userId: Long, accessHash: String): DbAuthorization {
        val auth = DbAuthorization(
            authorizationId = 0,
            userId = userId,
            accessHash = accessHash,
            refreshAccessHash = "test-refresh-hash",
            permissions = DbAuthorization.Permissions.All,
            expiresAt = System.currentTimeMillis() + 1.minutes.inWholeMilliseconds,
            createdAt = System.currentTimeMillis(),
            metaClientName = "test-client",
            metaClientVersion = 1.0,
            metaClientIpAddress = "localhost",
        )

        val id = datasource.createAuthorization(
            userId = auth.userId,
            accessHash = auth.accessHash,
            refreshAccessHash = auth.refreshAccessHash,
            permissions = auth.permissions,
            expiresAt = auth.expiresAt,
            createdAt = auth.createdAt,
            metaClientName = auth.metaClientName,
            metaClientVersion = auth.metaClientVersion,
            metaClientIpAddress = auth.metaClientIpAddress,
        )

        return auth.copy(authorizationId = id)
    }
}