package org.timemates.backend.auth.data.db.mapper

import org.jetbrains.exposed.sql.ResultRow
import org.timemates.backend.auth.data.db.entities.DbVerification
import org.timemates.backend.auth.data.db.table.VerificationSessionsTable

class DbVerificationsMapper {
    fun resultRowToDbVerification(resultRow: ResultRow): DbVerification = with(resultRow) {
        return DbVerification(
            verificationHash = get(VerificationSessionsTable.VERIFICATION_HASH),
            emailAddress = get(VerificationSessionsTable.EMAIL),
            code = get(VerificationSessionsTable.CONFIRMATION_CODE),
            attempts = get(VerificationSessionsTable.ATTEMPTS),
            time = get(VerificationSessionsTable.INIT_TIME),
            isConfirmed = get(VerificationSessionsTable.IS_CONFIRMED),
            metaClientName = get(VerificationSessionsTable.META_CLIENT_NAME),
            metaClientVersion = get(VerificationSessionsTable.META_CLIENT_VERSION),
            metaClientIpAddress = get(VerificationSessionsTable.META_CLIENT_IP_ADDRESS),
        )
    }
}