package io.timemates.backend.integrations.postgresql.repositories.datasource

import io.timemates.backend.integrations.postgresql.repositories.datasource.tables.VerificationsTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.minus
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class DbVerificationsDataSource(
    private val database: Database
) {
    suspend fun add(
        emailAddress: String,
        verificationToken: String,
        code: String,
        time: Long,
        attempts: Int
    ) = newSuspendedTransaction(db = database) {
        VerificationsTable.insert {
            it[EMAIL] = emailAddress
            it[VERIFICATION_TOKEN] = verificationToken
            it[CODE] = code
            it[TIME] = time
            it[ATTEMPTS] = attempts
        }
    }

    suspend fun addAttempt(verificationToken: String) {
        newSuspendedTransaction(db = database) {
            VerificationsTable.update(
                where = { VerificationsTable.VERIFICATION_TOKEN eq verificationToken },
                body = { it[ATTEMPTS] = ATTEMPTS - 1 }
            )
        }
    }

    suspend fun getVerification(verificationToken: String): Verification? =
        newSuspendedTransaction(db = database) {
            VerificationsTable.select {
                VerificationsTable.VERIFICATION_TOKEN eq verificationToken
            }.singleOrNull()?.toVerification()
        }

    suspend fun remove(verificationToken: String) {
        newSuspendedTransaction(db = database) {
            VerificationsTable.deleteWhere {
                VERIFICATION_TOKEN eq verificationToken
            }
        }
    }

    suspend fun getVerifications(emailAddress: String): List<Verification> =
        newSuspendedTransaction(db = database) {
            VerificationsTable.select {
                VerificationsTable.EMAIL eq emailAddress
            }.map { it.toVerification() }
        }

    suspend fun setConfirmed(verificationToken: String) =
        newSuspendedTransaction(db = database) {
            VerificationsTable.update {
                it[IS_CONFIRMED] = true
            }
        }

    suspend fun getCountOfConfirmation(emailAddress: String, afterTime: Long) =
        newSuspendedTransaction(db = database) {
            VerificationsTable.select {
                VerificationsTable.EMAIL eq emailAddress and (
                    VerificationsTable.TIME greater afterTime
                    )
            }.count()
        }

    suspend fun getAttemptsCount(
        emailAddress: String, afterTime: Long
    ) = newSuspendedTransaction(db = database) {
        VerificationsTable.slice(VerificationsTable.ATTEMPTS.sum()).select {
            VerificationsTable.EMAIL eq emailAddress and (
                VerificationsTable.TIME greater afterTime
                )
        }.singleOrNull()?.get(VerificationsTable.ATTEMPTS) ?: 0
    }


    class Verification(
        val emailAddress: String,
        val verificationToken: String,
        val code: String,
        val time: Long,
        val attempts: Int,
        val isConfirmed: Boolean
    )

    private fun ResultRow.toVerification(): Verification {
        return Verification(
            this[VerificationsTable.EMAIL],
            this[VerificationsTable.VERIFICATION_TOKEN],
            this[VerificationsTable.CODE],
            this[VerificationsTable.TIME],
            this[VerificationsTable.ATTEMPTS],
            this[VerificationsTable.IS_CONFIRMED]
        )
    }
}