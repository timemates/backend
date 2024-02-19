package org.timemates.backend.exposed

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table.Dual.default

/**
 * Denotes that a column has empty string value as default value.
 */
fun Column<String>.emptyAsDefault(): Column<String> = default(String())