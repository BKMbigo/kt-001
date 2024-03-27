package io.github.bkmbigo.gallery.processor.internal.verifiers.matcher

import io.github.bkmbigo.gallery.ksp.symbol.KSImportDirective
import io.github.bkmbigo.gallery.ksp.symbol.KSName
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.ParamWrapper
import io.github.bkmbigo.gallery.processor.internal.models.wrappers.StateComponentWrapper

internal data class ComponentMatched(
    val componentName: String,
    val fqName: KSName,
    val kDoc: String?,
    val importList: List<KSImportDirective>,
    val parameters: Map<ParamWrapper, StateComponentWrapper>
)
