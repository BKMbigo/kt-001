package io.github.bkmbigo.gallery.processor.internal.models.wrappers

import io.github.bkmbigo.gallery.ksp.symbol.KSImportDirective
import io.github.bkmbigo.gallery.ksp.symbol.KSName
import io.github.bkmbigo.gallery.ksp.symbol.KSType

internal data class ComponentThemeWrapper(
    val componentId: String,
    val kDoc: String?,
    val fqName: KSName,
    val contentParameterName: String,
    val importList: List<KSImportDirective>,
    val parameters: List<ParamWrapper>
)
