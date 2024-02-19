package org.timemates.backend.users.data.datasource

import io.github.reactivecircus.cache4k.Cache

class CachedUsersDataSource(
    maxEntries: Long,
) {
    private val cache = Cache.Builder()
        .maximumCacheSize(maxEntries)
        .build<Long, User>()

    fun getUser(userId: Long) = cache.get(userId)
    fun putUser(id: Long, user: User) = cache.put(id, user)
    fun getUsers(ids: List<Long>) = ids.associateWith {
        cache.get(it)
    }

    fun invalidateUser(userId: Long) = cache.invalidate(userId)

    data class User(
        val name: String,
        val shortBio: String?,
        val avatarFileId: String?,
        val gravatarId: String?,
        val email: String?,
    )
}