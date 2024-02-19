package org.timemates.backend.auth.data.test.db

import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.Database
import org.timemates.backend.auth.data.db.TableVerificationsDataSource
import org.timemates.backend.auth.data.db.entities.DbVerification
import org.timemates.backend.auth.data.db.mapper.DbVerificationsMapper
import kotlin.test.*

class TableVerificationsDataSourceTest {
    private val databaseUrl = "jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;"
    private val databaseDriver = "org.h2.Driver"

    private val database = Database.connect(databaseUrl, databaseDriver)
    private val verificationsMapper = DbVerificationsMapper()

    private val datasource = TableVerificationsDataSource(database, verificationsMapper)

    @BeforeTest
    @AfterTest
    fun `clean table`() = runTest {
        datasource.clearAll()
    }

    @Test
    fun `check add creates a verification session correctly`() = runTest {
        val emailAddress = "test@example.com"
        val verificationToken = "verification-token"
        val code = "123456"
        val time = System.currentTimeMillis()
        val attempts = 3
        val metaClientName = "test-client"
        val metaClientVersion = 1.0
        val metaClientIpAddress = "localhost"

        datasource.add(
            emailAddress = emailAddress,
            verificationToken = verificationToken,
            code = code,
            time = time,
            attempts = attempts,
            metaClientName = metaClientName,
            metaClientVersion = metaClientVersion,
            metaClientIpAddress = metaClientIpAddress
        )

        val result = datasource.getVerification(verificationToken)

        assertNotNull(result)
        assertEquals(expected = emailAddress, actual = result.emailAddress)
        assertEquals(expected = verificationToken, actual = result.verificationHash)
        assertFalse(result.isConfirmed)
        assertEquals(expected = code, actual = result.code)
        assertEquals(expected = attempts, actual = result.attempts)
        assertEquals(expected = time, actual = result.time)
        assertEquals(expected = metaClientName, actual = result.metaClientName)
        assertEquals(expected = metaClientVersion, actual = result.metaClientVersion)
        assertEquals(expected = metaClientIpAddress, actual = result.metaClientIpAddress)
    }

    @Test
    fun `check getVerification returns correct result`() = runTest {
        val verificationHash = "verification-hash"
        val dbVerification = DbVerification(
            emailAddress = "test@example.com",
            verificationHash = verificationHash,
            isConfirmed = false,
            code = "123456",
            attempts = 3,
            time = System.currentTimeMillis(),
            metaClientName = "test-client",
            metaClientVersion = 1.0,
            metaClientIpAddress = "localhost",
        )

        datasource.add(
            emailAddress = dbVerification.emailAddress,
            verificationToken = dbVerification.verificationHash,
            code = dbVerification.code,
            time = dbVerification.time,
            attempts = dbVerification.attempts,
            metaClientName = dbVerification.metaClientName,
            metaClientVersion = dbVerification.metaClientVersion,
            metaClientIpAddress = dbVerification.metaClientIpAddress,
        )

        val result = datasource.getVerification(verificationHash)

        assertEquals(expected = dbVerification, actual = result)
    }

    @Test
    fun `check decreaseAttempts updates attempts correctly`() = runTest {
        val verificationHash = "verification-hash"
        val initialAttempts = 3

        datasource.add(
            emailAddress = "test@example.com",
            verificationToken = verificationHash,
            code = "123456",
            time = System.currentTimeMillis(),
            attempts = initialAttempts,
            metaClientName = "test-client",
            metaClientVersion = 1.0,
            metaClientIpAddress = "localhost"
        )

        datasource.decreaseAttempts(verificationHash)

        val result = datasource.getVerification(verificationHash)

        assertNotNull(result)
        assertEquals(expected = initialAttempts - 1, actual = result.attempts)
    }

    @Test
    fun `check getAttempts returns correct sum of attempts`() = runTest {
        val emailAddress = "test@example.com"
        val afterTime = System.currentTimeMillis()

        datasource.add(
            emailAddress = emailAddress,
            verificationToken = "verification-hash1",
            code = "123456",
            time = afterTime + 1000,
            attempts = 5,
            metaClientName = "test-client",
            metaClientVersion = 1.0,
            metaClientIpAddress = "localhost"
        )
        datasource.add(
            emailAddress = emailAddress,
            verificationToken = "verification-hash2",
            code = "123456",
            time = afterTime + 2000,
            attempts = 2,
            metaClientName = "test-client",
            metaClientVersion = 1.0,
            metaClientIpAddress = "localhost"
        )

        val result = datasource.getAttempts(emailAddress, afterTime)

        assertEquals(expected = 7, actual = result)
    }

    @Test
    fun `check getSessionsCount returns correct count`() = runTest {
        val emailAddress = "test@example.com"
        val afterTime = System.currentTimeMillis()

        datasource.add(
            emailAddress = emailAddress,
            verificationToken = "verification-hash1",
            code = "123456",
            time = afterTime + 1000,
            attempts = 1,
            metaClientName = "test-client",
            metaClientVersion = 1.0,
            metaClientIpAddress = "localhost"
        )
        datasource.add(
            emailAddress = emailAddress,
            verificationToken = "verification-hash2",
            code = "123456",
            time = afterTime + 2000,
            attempts = 1,
            metaClientName = "test-client",
            metaClientVersion = 1.0,
            metaClientIpAddress = "localhost"
        )

        val result = datasource.getSessionsCount(emailAddress, afterTime)

        assertEquals(expected = 2, actual = result)
    }

    @Test
    fun `check setAsConfirmed updates verification session correctly`() = runTest {
        val verificationHash = "verification-hash"

        datasource.add(
            emailAddress = "test@example.com",
            verificationToken = verificationHash,
            code = "123456",
            time = System.currentTimeMillis(),
            attempts = 1,
            metaClientName = "test-client",
            metaClientVersion = 1.0,
            metaClientIpAddress = "localhost"
        )

        val result = datasource.setAsConfirmed(verificationHash)

        assertTrue(result)

        val updatedSession = datasource.getVerification(verificationHash)

        assertNotNull(updatedSession)
        assertTrue(updatedSession.isConfirmed)
    }

    @Test
    fun `check setAsConfirmed returns false if verification session does not exist`() = runTest {
        val verificationHash = "non-existent-hash"

        val result = datasource.setAsConfirmed(verificationHash)

        assertFalse(result)
    }

    @Test
    fun `check remove deletes verification session correctly`() = runTest {
        val verificationHash = "verification-hash"

        datasource.add(
            emailAddress = "test@example.com",
            verificationToken = verificationHash,
            code = "123456",
            time = System.currentTimeMillis(),
            attempts = 1,
            metaClientName = "test-client",
            metaClientVersion = 1.0,
            metaClientIpAddress = "localhost"
        )

        datasource.remove(verificationHash)

        val result = datasource.getVerification(verificationHash)

        assertNull(result)
    }
}
