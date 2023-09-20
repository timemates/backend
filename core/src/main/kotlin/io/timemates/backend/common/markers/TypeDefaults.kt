package io.timemates.backend.common.markers

import io.timemates.backend.validation.markers.InternalThrowAbility

/**
 * Interface-marker for companions that have default variant of type (entity). This interface
 * marks that it's safe place to construct types in unsafe way or throw internal exceptions (as it's
 * not intended at all and should be handled properly)
 */
interface TypeDefaults : InternalThrowAbility