package io.timemates.backend.auth.data.db

import io.timemates.backend.auth.data.db.entities.DbVerification
import io.timemates.backend.auth.data.db.mapper.DbVerificationsMapper
import io.timemates.backend.auth.data.db.table.VerificationSessionsTable
import io.timemates.backend.auth.data.db.table.VerificationSessionsTable.ATTEMPTS
import io.timemates.backend.auth.data.db.table.VerificationSessionsTable.VERIFICATION_HASH
import io.timemates.backend.exposed.suspendedTransaction
import io.timemates.backend.exposed.update
import org.jetbrains.annotations.TestOnly
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class TableVerificationsDataSource(
    private val database: Database,
    private val verificationsMapper: DbVerificationsMapper,
) {

    init {
        transaction(database) {
            SchemaUtils.create(VerificationSessionsTable)
        }
    }

    suspend fun add(
        emailAddress: String,
        verificationToken: String,
        code: String,
        time: Long,
        attempts: Int,
        metaClientName: String,
        metaClientVersion: Double,
        metaClientIpAddress: String,
    ): Unit = suspendedTransaction(database) {
        VerificationSessionsTable.insert {
            it[EMAIL] = emailAddress
            it[VERIFICATION_HASH] = verificationToken
            it[IS_CONFIRMED] = false
            it[CONFIRMATION_CODE] = code
            it[ATTEMPTS] = attempts
            it[INIT_TIME] = time
            it[META_CLIENT_NAME] = metaClientName
            it[META_CLIENT_VERSION] = metaClientVersion
            it[META_CLIENT_IP_ADDRESS] = metaClientIpAddress
        }
    }

    suspend fun getVerification(verificationHash: String): DbVerification? = suspendedTransaction(database) {
        VerificationSessionsTable.select { VERIFICATION_HASH eq verificationHash }
            .singleOrNull()
            ?.let(verificationsMapper::resultRowToDbVerification)
    }

    suspend fun decreaseAttempts(verificationHash: String): Unit = suspendedTransaction(database) {
        val current = VerificationSessionsTable.select { VERIFICATION_HASH eq verificationHash }
            .singleOrNull()
            ?.get(ATTEMPTS)
            ?: error("Cannot decrease number of attempts, as there is no such verification session")

        VerificationSessionsTable.update(VERIFICATION_HASH eq verificationHash) {
            it[ATTEMPTS] = current - 1
        }
    }

    suspend fun getAttempts(email: String, afterTime: Long): Int = try {
        suspendedTransaction(database) {
            VerificationSessionsTable.select {
                VerificationSessionsTable.EMAIL eq email and
                    (VerificationSessionsTable.INIT_TIME greater afterTime)
            }.sumOf { it[ATTEMPTS] }
        }
    } catch (e: Throwable) {
        e.printStackTrace()
        0
    }

    suspend fun getSessionsCount(
        email: String,
        afterTime: Long,
    ): Int = suspendedTransaction(database) {
        VerificationSessionsTable.select {
            VerificationSessionsTable.EMAIL eq email and
                (VerificationSessionsTable.INIT_TIME greater afterTime)
        }.count().toInt()
    }

    suspend fun setAsConfirmed(verificationHash: String): Boolean = suspendedTransaction(database) {
        VerificationSessionsTable.update(VERIFICATION_HASH eq verificationHash) {
            it[IS_CONFIRMED] = true
        } > 0
    }

    suspend fun remove(verificationHash: String): Unit = suspendedTransaction(database) {
        VerificationSessionsTable.deleteWhere { VERIFICATION_HASH eq verificationHash }
    }

    @TestOnly
    suspend fun clearAll(): Unit = suspendedTransaction(database) {
        VerificationSessionsTable.deleteAll()
    }
}