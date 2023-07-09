package timemates.backend.hashing

import java.math.BigInteger
import java.security.MessageDigest
import timemates.backend.hashing.repository.HashingRepository as HashingRepositoryContract

class HashingRepository : HashingRepositoryContract {
    override fun generateMD5Hash(value: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(
            1, md.digest(value.trim().lowercase().toByteArray())
        ).toString(16).padStart(32, '0')
    }
}