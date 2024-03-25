package io.github.bkmbigo.gallery.processor.internal.models.savers

import kotlinx.serialization.Serializable

@Serializable
internal data class StateComponentWrapperSaver(
    val baseTypeFqName: String,
    val fqName: String,
    val isDefault: Boolean = true,
    val identifier: String? = null
)
