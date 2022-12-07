package org.tomadoro.backend.repositories.integration.internal

import org.jetbrains.exposed.sql.Query

fun Query.limit(boundary: IntProgression) = limit(
    boundary.last - boundary.first,
    boundary.first.toLong()
)
