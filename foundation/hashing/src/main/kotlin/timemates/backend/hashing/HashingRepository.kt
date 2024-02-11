package timemates.backend.hashing

import java.math.BigInteger
import java.security.MessageDigest

class HashingRepository {
    fun md5(value: String): Hash {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(
            1, md.digest(value.trim().lowercase().toByteArray())
        ).toString(16).padStart(32, '0').let {
            Hash(it)
        }
    }
}