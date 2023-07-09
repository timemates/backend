package timemates.backend.hashing.repository

interface HashingRepository {

    fun generateMD5Hash(value: String): String
}