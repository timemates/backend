package io.timemates.backend.endpoints.types.value

import kotlinx.serialization.Serializable
import io.timemates.backend.types.value.Count as DomainCount

@Serializable
@JvmInline
value class Count(val int: Int) {
    init {
        require(int >= 0) { "Count should be equal or be bigger than zero" }
    }
}

fun DomainCount.serializable(): Count = Count(int)
fun Count.internal(): DomainCount = DomainCount(int)