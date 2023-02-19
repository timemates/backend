package io.timemates.backend.integrations.cache.storage

import io.github.reactivecircus.cache4k.Cache

class UsersCacheDataSource(
    maxEntries: Long
) {
    private val cache = Cache.Builder()
        .maximumCacheSize(maxEntries)
        .build<Int, User>()

    fun getUser(userId: Int) = cache.get(userId)
    fun putUser(id: Int, user: User) = cache.put(id, user)
    fun getUsers(ids: Collection<Int>) = ids.associateWith {
        cache.get(it)
    }

    fun invalidateUser(userId: Int) = cache.invalidate(userId)

    data class User(
        val name: String,
        val shortBio: String?,
        val avatarFileId: String?,
        val email: String
    )
}