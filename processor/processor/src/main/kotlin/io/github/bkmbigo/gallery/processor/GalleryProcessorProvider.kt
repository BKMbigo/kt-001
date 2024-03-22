package io.github.bkmbigo.gallery.processor

import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

@AutoService(SymbolProcessorProvider::class)
class GalleryProcessorProvider: SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        GalleryProcessor(
            logger = environment.logger,
            codeGenerator = environment.codeGenerator
        )
}
