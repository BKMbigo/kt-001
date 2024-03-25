package io.github.bkmbigo.gallery.processor

import io.github.bkmbigo.gallery.ksp.processing.*
import io.github.bkmbigo.gallery.ksp.symbol.KSAnnotated
import io.github.bkmbigo.gallery.ksp.symbol.KSFunctionDeclaration
import io.github.bkmbigo.gallery.processor.internal.Constants
import io.github.bkmbigo.gallery.processor.internal.environment.createDefaultProcessorEnvironment
import io.github.bkmbigo.gallery.processor.internal.models.StateComponentMap
import io.github.bkmbigo.gallery.processor.internal.verifiers.processStateComponent
import java.io.File

class GalleryProcessor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator
): SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> = createDefaultProcessorEnvironment(logger, resolver) {
        // Retrieve the past stateComponentMap state

        val stateComponentMap = StateComponentMap()

        resolver
            .getSymbolsWithAnnotation(Constants.Annotations.FQName.GalleryStatePage)
            .filterIsInstance<KSFunctionDeclaration>()
            .forEach {
                val stateComponentWrapper = it.processStateComponent()
            }

        resolver
            .getSymbolsWithAnnotation(Constants.Annotations.FQName.GalleryStateRow)
            .filterIsInstance<KSFunctionDeclaration>()
            .forEach {
                val stateComponentWrapper = it.processStateComponent()
            }




        emptyList()
    }
}
