package io.github.bkmbigo.gallery.processor.internal.utils

import io.github.bkmbigo.gallery.ksp.symbol.KSFunctionDeclaration
import io.github.bkmbigo.gallery.processor.internal.Constants

internal fun KSFunctionDeclaration.isComposable(): Boolean =
    annotations.toList().any { it.shortName.getShortName() == Constants.Annotations.SimpleName.Composable }

internal fun KSFunctionDeclaration.allParametersHaveDefaultValues(): Boolean =
    parameters.all { it.hasDefault }
