package io.github.bkmbigo.gallery.processor.internal.models.wrappers

import io.github.bkmbigo.gallery.ksp.symbol.KSName
import io.github.bkmbigo.gallery.ksp.symbol.KSType

internal data class PageSubstituteWrapper(
    val fqName: KSName,
    // For future plans --> A User might want to declare a specific @GalleryPageSubstitute for a specific type
    val type: KSType,
    val paramNameParameterName: String?,
    val onNavigateToScreenParameterName: String
)
