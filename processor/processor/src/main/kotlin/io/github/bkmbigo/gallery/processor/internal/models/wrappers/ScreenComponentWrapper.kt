package io.github.bkmbigo.gallery.processor.internal.models.wrappers

import io.github.bkmbigo.gallery.ksp.symbol.KSName

internal data class ScreenComponentWrapper(
    val fqName: KSName,
    val componentParameterName: String,
    val stateComponentsParameterName: String,
    val componentNameParameterName: String?,
    val themeStateComponentsParameterName: String?,
    val onNavigateBackParameterName: String?,
    val hasStateComponentsParameterName: String?,
    val hasThemeComponentParameterName: String?
)
