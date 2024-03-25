package io.github.bkmbigo.gallery.processor.internal.models.savers

import kotlinx.serialization.Serializable

@Serializable
internal data class StateComponentTypeListSaver(
    val type: String,
    val defaultComponent: StateComponentWrapperSaver?,
    val identifiedStateComponents: Map<String, StateComponentWrapperSaver>
)
