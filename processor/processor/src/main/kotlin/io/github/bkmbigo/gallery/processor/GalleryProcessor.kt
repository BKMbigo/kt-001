package io.github.bkmbigo.gallery.processor

import io.github.bkmbigo.gallery.ksp.processing.*
import io.github.bkmbigo.gallery.ksp.symbol.KSAnnotated
import io.github.bkmbigo.gallery.ksp.symbol.KSFunctionDeclaration
import io.github.bkmbigo.gallery.processor.internal.Constants
import io.github.bkmbigo.gallery.processor.internal.GalleryProcessorException
import io.github.bkmbigo.gallery.processor.internal.codegenerator.generateComponentScreenFunction
import io.github.bkmbigo.gallery.processor.internal.environment.createDefaultProcessorEnvironment
import io.github.bkmbigo.gallery.processor.internal.models.ComponentRegistrar
import io.github.bkmbigo.gallery.processor.internal.models.StateComponentMap
import io.github.bkmbigo.gallery.processor.internal.verifiers.*
import io.github.bkmbigo.gallery.processor.internal.verifiers.matcher.matchParameters
import io.github.bkmbigo.gallery.processor.internal.verifiers.processComponent
import io.github.bkmbigo.gallery.processor.internal.verifiers.processComponentTheme
import io.github.bkmbigo.gallery.processor.internal.verifiers.processScreenComponent
import io.github.bkmbigo.gallery.processor.internal.verifiers.processStateComponent

class GalleryProcessor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator
): SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> = createDefaultProcessorEnvironment(logger, resolver) {
        // I have intentionally disabled incremental compilation... This is because I believe that, FileToFile map and FileToSymbolMap used by Kotlin Symbol Processing (KSP) cannot be used to track additional @GalleryStateComponents

        val stateComponentMap = StateComponentMap()
        val componentRegistrar = ComponentRegistrar()

        resolver
            .getSymbolsWithAnnotation(Constants.Annotations.FQName.GalleryStatePage)
            .filterIsInstance<KSFunctionDeclaration>()
            .forEach {
                val stateComponentWrapper = it.processStateComponent()
                stateComponentMap.addStateComponent(stateComponentWrapper)
            }

        resolver
            .getSymbolsWithAnnotation(Constants.Annotations.FQName.GalleryStateRow)
            .filterIsInstance<KSFunctionDeclaration>()
            .forEach {
                val stateComponentWrapper = it.processStateComponent()
                stateComponentMap.addStateComponent(stateComponentWrapper)
            }


        // Process new @GalleryComponent
        resolver.getSymbolsWithAnnotation(Constants.Annotations.FQName.GalleryComponentTheme)
            .filterIsInstance<KSFunctionDeclaration>()
            .forEach {
                val componentTheme = it.processComponentTheme()
                val matchedComponentTheme = componentTheme.matchParameters(stateComponentMap)
                componentRegistrar.addComponentTheme(matchedComponentTheme)
            }

        resolver.getSymbolsWithAnnotation(Constants.Annotations.FQName.GalleryComponent)
            .filterIsInstance<KSFunctionDeclaration>()
            .forEach {
                val component = it.processComponent()
                val matchedComponent = component.matchParameters(stateComponentMap)
                componentRegistrar.addComponent(matchedComponent)
            }


        // Design annotations
        resolver.getSymbolsWithAnnotation(Constants.Annotations.FQName.GalleryScreen)
            .filterIsInstance<KSFunctionDeclaration>()
            .forEach {
                val screenComponent = it.processScreenComponent()
                componentRegistrar.registerGalleryScreen(screenComponent)
            }

        // @GalleryComponentSelectionScreen
        resolver.getSymbolsWithAnnotation(Constants.Annotations.FQName.GalleryComponentSelectionScreen)
            .filterIsInstance<KSFunctionDeclaration>()
            .forEach {
                val screenComponent = it.processScreenComponentSelectionScreen()
                componentRegistrar.registerScreenComponentSelectionScreen(screenComponent)
            }


        // At this point, the following conditions have to be met:
        //      There should be a registered @GalleryScreen Component
        if (!componentRegistrar.hasScreen) {
            logger.error("The project does not have a @GalleryScreen component. Please add it manually or register a library/module containing a @GalleryScreen")
            throw GalleryProcessorException()
        }

        if (!componentRegistrar.hasScreenComponentSelectionScreen) {
            logger.error("The project does not have a @GalleryComponentSelectionScreen component. Please add it manually or register a library/module providing it")
            throw GalleryProcessorException()
        }

        // Produce @GalleryComponent files
        componentRegistrar.components.forEach { component ->
            codeGenerator.createNewFile(
                dependencies = Dependencies.ALL_FILES, // I have disabled KSP's incremental compilation
                packageName = component.fqName.getQualifier(),
                fileName = "${component.fqName.getShortName()}ComponentScreen"
            ).apply {
                writer().use {
                    it.append(generateComponentScreenFunction(componentRegistrar.screen!!, component))
                }
            }
        }

        // Produce Navigation Component


        // Produce Main Component


        logger.error("Success")
        emptyList()
    }
}
