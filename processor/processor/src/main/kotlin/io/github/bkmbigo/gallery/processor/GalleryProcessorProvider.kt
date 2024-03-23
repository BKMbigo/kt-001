package io.github.bkmbigo.gallery.processor

import io.github.bkmbigo.gallery.ksp.processing.SymbolProcessor
import io.github.bkmbigo.gallery.ksp.processing.SymbolProcessorEnvironment
import io.github.bkmbigo.gallery.ksp.processing.SymbolProcessorProvider

class GalleryProcessorProvider: SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        GalleryProcessor(
            logger = environment.logger,
            codeGenerator = environment.codeGenerator
        )
}
