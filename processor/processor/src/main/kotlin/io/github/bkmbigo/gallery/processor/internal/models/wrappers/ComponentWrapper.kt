package io.github.bkmbigo.gallery.processor.internal.models.wrappers

import io.github.bkmbigo.gallery.ksp.symbol.KSName
import io.github.bkmbigo.gallery.ksp.symbol.KSType

internal data class ComponentWrapper(
    val fqName: KSName,
    val kDoc: String,
    val paramTypes: List<KSType>
)
