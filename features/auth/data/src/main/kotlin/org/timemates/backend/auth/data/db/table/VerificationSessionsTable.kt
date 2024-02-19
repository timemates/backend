package org.timemates.backend.auth.data.db.table

import org.jetbrains.exposed.sql.Table
import org.timemates.backend.types.auth.value.VerificationCode
import org.timemates.backend.types.auth.value.VerificationHash
import org.timemates.backend.types.users.value.EmailAddress

object VerificationSessionsTable : Table("verification_sessions") {
    val USER_ID = long("user_id").nullable()
    val VERIFICATION_HASH = varchar("verification_hash", VerificationHash.SIZE)
    val EMAIL = varchar("email", EmailAddress.SIZE.last)
    val IS_CONFIRMED = bool("is_confirmed").default(false)
    val CONFIRMATION_CODE = varchar("confirmation_code", VerificationCode.SIZE)
    val ATTEMPTS = integer("attempts")
    val INIT_TIME = long("init_time")
    val META_CLIENT_NAME = varchar("meta_client_name", 128)
    val META_CLIENT_VERSION = double("meta_client_version")
    val META_CLIENT_IP_ADDRESS = varchar("meta_client_ip_address", 128)
}