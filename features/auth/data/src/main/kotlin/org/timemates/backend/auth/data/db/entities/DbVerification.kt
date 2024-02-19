package org.timemates.backend.auth.data.db.entities

data class DbVerification(
    val verificationHash: String,
    val emailAddress: String,
    val code: String,
    val attempts: Int,
    val time: Long,
    val isConfirmed: Boolean,
    val metaClientName: String,
    val metaClientVersion: Double,
    val metaClientIpAddress: String,
)