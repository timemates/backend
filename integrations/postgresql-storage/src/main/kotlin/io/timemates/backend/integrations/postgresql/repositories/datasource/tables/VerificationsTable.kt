package io.timemates.backend.integrations.postgresql.repositories.datasource.tables

import org.jetbrains.exposed.sql.Table

object VerificationsTable : Table("verifications") {
    val EMAIL = varchar("email", 200)
    val CODE = varchar("code", 5)
    val VERIFICATION_TOKEN = varchar("verification_token", 200)
    val ATTEMPTS = integer("attempts")
    val TIME = long("time")
    val IS_CONFIRMED = bool("is_confirmed")
}