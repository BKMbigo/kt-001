package io.github.bkmbigo.gallery.processor.internal.models.wrappers

import io.github.bkmbigo.gallery.ksp.symbol.KSExpression
import io.github.bkmbigo.gallery.ksp.symbol.KSName
import io.github.bkmbigo.gallery.ksp.symbol.KSType

data class ParamWrapper(
    val identifier: String?,
    val paramName: String?,
    val name: KSName,
    val type: KSType,
    val defaultExpression: KSExpression?
)
