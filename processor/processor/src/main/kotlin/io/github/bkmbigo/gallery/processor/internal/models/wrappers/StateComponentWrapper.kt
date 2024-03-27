package io.github.bkmbigo.gallery.processor.internal.models.wrappers

import io.github.bkmbigo.gallery.ksp.symbol.KSName
import io.github.bkmbigo.gallery.ksp.symbol.KSType

internal data class StateComponentWrapper(
    val isRow: Boolean,
    val fqName: KSName,
    val type: KSType,
    val isDefault: Boolean = true,
    val identifier: String? = null,
    val stateParameterName: String,
    val onStateParameterName: String
)
