package io.github.bkmbigo.gallery.processor.internal.environment

import io.github.bkmbigo.gallery.ksp.processing.KSBuiltIns
import io.github.bkmbigo.gallery.ksp.processing.KSPLogger
import io.github.bkmbigo.gallery.ksp.symbol.KSClassDeclaration
import io.github.bkmbigo.gallery.ksp.symbol.KSName
import io.github.bkmbigo.gallery.ksp.symbol.KSType


internal interface ProcessorEnvironment {

    val logger: KSPLogger

    val builtIns: KSBuiltIns

}
